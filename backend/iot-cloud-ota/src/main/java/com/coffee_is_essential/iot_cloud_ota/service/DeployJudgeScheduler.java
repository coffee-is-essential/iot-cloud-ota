package com.coffee_is_essential.iot_cloud_ota.service;

import com.coffee_is_essential.iot_cloud_ota.entity.*;
import com.coffee_is_essential.iot_cloud_ota.enums.DeploymentStatus;
import com.coffee_is_essential.iot_cloud_ota.enums.OverallStatus;
import com.coffee_is_essential.iot_cloud_ota.repository.*;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * 배포 상태를 주기적으로 점검하는 스케줄러 서비스
 * Redis에 저장된 배포 키(commandId)를 기반으로 각 디바이스의 다운로드 이벤트를 조회하고,
 * 배포 완료 / 실패 / 타임아웃 여부를 판별하여 DB에 반영함
 * 배포별로 독립된 스케줄러를 관리하며, 조건이 충족되면 자동으로 스케줄러를 종료함
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DeployJudgeScheduler {
    private final StringRedisTemplate srt;
    private final FirmwareDeploymentRepository firmwareDeploymentRepository;
    private final FirmwareDeploymentDeviceRepository firmwareDeploymentDeviceRepository;
    private final OverallDeploymentStatusRepository overallDeploymentStatusRepository;
    private final DeploymentRedisService deploymentRedisService;
    private final EntityManager em;
    private final DownloadEventsJdbcRepository downloadEventsJdbcRepository;
    private final DeviceService deviceService;

    private final ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
    private final ConcurrentHashMap<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        taskScheduler.setPoolSize(10);
        taskScheduler.initialize();
    }

    /**
     * 특정 배포(commandId)에 대한 스케줄러를 시작한다.
     * 30초마다 dumpDeploySet을 실행하며,
     * 이미 해당 commandId의 스케줄러가 실행 중이면 새로 시작하지 않음
     *
     * @param commandId 배포 식별자
     */
    public synchronized void startScheduler(String commandId) {
        if (scheduledTasks.containsKey(commandId)) {
            log.warn("[WARN] Scheduler already running for commandId={}", commandId);
            return;
        }
        ScheduledFuture<?> future = taskScheduler.scheduleAtFixedRate(
                () -> dumpDeploySet(commandId),
                Duration.ofSeconds(60)
        );
        scheduledTasks.put(commandId, future);
        log.info("[START] Scheduler started for commandId={}", commandId);
    }

    /**
     * 특정 배포(commandId)에 대한 스케줄러를 중지한다.
     * 맵에서 해당 commandId의 ScheduledFuture를 제거하고 cancel 처리한다.
     *
     * @param commandId 배포 식별자
     */
    public synchronized void stopScheduler(String commandId) {
        ScheduledFuture<?> future = scheduledTasks.remove(commandId);
        if (future != null) {
            future.cancel(false);
            log.info("[STOP] Scheduler stopped for commandId={}", commandId);
        }
    }

    /**
     * Redis에서 특정 배포(commandId)의 디바이스 목록을 스캔하여 상태를 점검한다.
     * Redis에 디바이스 ID가 없으면 스케줄러를 종료하고,
     * 존재하면 judge 메서드를 호출한다.
     *
     * @param commandId 배포 식별자
     */
    private void dumpDeploySet(String commandId) {
        try {
            List<Long> deviceIds = deploymentRedisService.getAllDeviceIdsFromRedisById(commandId);
            if (deviceIds.isEmpty()) {
                log.info("[ERROR] No devices left in Redis for commandId={}, stopping scheduler", commandId);
                stopScheduler(commandId);
                return;
            }
            judge(commandId, deviceIds);
        } catch (Exception e) {
            log.error("[ERROR] Error while scanning Redis for commandId={}, err={}", commandId, e.getMessage(), e);
        }
    }

    /**
     * 특정 배포(commandId)의 디바이스 상태를 판별한다.
     * QuestDB에서 최신 다운로드 이벤트를 조회하여 완료된 이벤트를 DB에 반영하고,
     * Redis에서 제거한다. 모든 디바이스가 완료되었거나 만료 시간이 지난 경우
     * 전체 배포 상태를 COMPLETED로 저장하고 스케줄러를 종료한다.
     *
     * @param commandId 배포 식별자
     * @param deviceIds 배포에 포함된 디바이스 ID 리스트
     */
    @Transactional
    public void judge(String commandId, List<Long> deviceIds) {
        FirmwareDeployment firmwareDeployment = firmwareDeploymentRepository.findByCommandIdOrElseThrow(commandId);
        OffsetDateTime expiresAt = firmwareDeployment.getExpiresAt();

        log.info(
                "[CHECKING] Judge started for commandId={}, deviceCount={}, expiresAt={}, deviceIds={}",
                commandId,
                deviceIds.size(),
                expiresAt.toInstant(),
                deviceIds
        );

        List<FirmwareDownloadEvents> downloadEvents = downloadEventsJdbcRepository.findLatestPerDeviceByCommandIdAndDeviceIds(commandId, deviceIds);
        List<FirmwareDownloadEvents> completedEvents = downloadEvents.stream()
                .filter(e -> isCompleted(e.getStatus()))
                .toList();

        if (!completedEvents.isEmpty()) {
            processCompletedEvents(commandId, completedEvents, firmwareDeployment);
            String type = commandId.split("-")[0];
            if ("AD".equals(type)) {
                deviceService.updateDeviceAds(commandId, completedEvents);
            } else if ("FW".equals(type)) {
                deviceService.updateDeviceFirmware(commandId, completedEvents);
            }
        }

        Long size = srt.opsForSet().size(commandId);
        if (size == null || size == 0) {
            log.info("[SUCCESS] Deployment completed successfully, commandId={}", commandId);
            overallDeploymentStatusRepository.save(
                    new OverallDeploymentStatus(firmwareDeployment, OverallStatus.COMPLETED)
            );
            stopScheduler(commandId);
            return;
        }

        if (Instant.now().isAfter(expiresAt.toInstant())) {
            log.warn("[TIMEOUT] Deployment expired (timeout), commandId={}", commandId);
            processTimeoutEvents(commandId, firmwareDeployment);
            overallDeploymentStatusRepository.save(
                    new OverallDeploymentStatus(firmwareDeployment, OverallStatus.COMPLETED)
            );
            stopScheduler(commandId);
        }
    }

    /**
     * 만료된 배포(commandId)에 대해 TIMEOUT 처리한다.
     * 남은 모든 디바이스를 TIMEOUT 상태로 저장하고,
     * Redis에도 반영한 뒤 Redis key를 삭제한다.
     *
     * @param commandId  배포 식별자
     * @param deployment 배포 엔티티
     */
    private void processTimeoutEvents(String commandId, FirmwareDeployment deployment) {
        List<Long> deviceIds = deploymentRedisService.getAllDeviceIdsFromRedisById(commandId);
        List<FirmwareDeploymentDevice> list = deviceIds.stream()
                .map(e -> new FirmwareDeploymentDevice(
                        em.getReference(Device.class, e),
                        deployment,
                        DeploymentStatus.TIMEOUT
                ))
                .toList();
        firmwareDeploymentDeviceRepository.saveAll(list);
        deploymentRedisService.saveTimeoutDevices(commandId, deviceIds);
        srt.delete(commandId);
    }

    /**
     * 완료된 디바이스 이벤트를 DB에 반영하고 Redis에서 제거한다.
     *
     * @param commandId       배포 식별자
     * @param completedEvents 완료된 이벤트 목록
     * @param deployment      배포 엔티티
     */
    private void processCompletedEvents(String commandId, List<FirmwareDownloadEvents> completedEvents, FirmwareDeployment deployment) {
        List<FirmwareDeploymentDevice> list = completedEvents.stream()
                .map(e -> new FirmwareDeploymentDevice(
                        em.getReference(Device.class, e.getDeviceId()),
                        deployment,
                        DeploymentStatus.valueOf(e.getStatus())
                ))
                .toList();
        firmwareDeploymentDeviceRepository.saveAll(list);
        deploymentRedisService.deleteDevices(commandId, completedEvents);
    }

    /**
     * 이벤트 상태가 완료 상태(SUCCESS, FAILED, CANCELLED, TIMEOUT)인지 여부를 판별한다.
     *
     * @param deploymentStatus 이벤트 상태 문자열
     * @return 완료 상태이면 true, 아니면 false
     */
    private boolean isCompleted(String deploymentStatus) {
        return deploymentStatus.equals(DeploymentStatus.SUCCESS.name()) || deploymentStatus.equals(DeploymentStatus.FAILED.name())
               || deploymentStatus.equals(DeploymentStatus.CANCELLED.name()) || deploymentStatus.equals(DeploymentStatus.TIMEOUT.name());
    }
}
