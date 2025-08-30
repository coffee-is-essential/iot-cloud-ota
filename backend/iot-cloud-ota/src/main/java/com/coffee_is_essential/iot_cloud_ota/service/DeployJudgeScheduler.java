package com.coffee_is_essential.iot_cloud_ota.service;

import com.coffee_is_essential.iot_cloud_ota.entity.*;
import com.coffee_is_essential.iot_cloud_ota.enums.DeploymentStatus;
import com.coffee_is_essential.iot_cloud_ota.enums.OverallStatus;
import com.coffee_is_essential.iot_cloud_ota.repository.FirmwareDeploymentDeviceRepository;
import com.coffee_is_essential.iot_cloud_ota.repository.FirmwareDeploymentRepository;
import com.coffee_is_essential.iot_cloud_ota.repository.OverallDeploymentStatusRepository;
import com.coffee_is_essential.iot_cloud_ota.repository.QuestDbRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 배포 상태를 주기적으로 점검하는 스케줄러 서비스.
 * Redis에 저장된 배포 키(commandId)를 순회하면서 각 디바이스의 다운로드 이벤트를 조회하고,
 * 배포 완료 / 실패 / 타임아웃 여부를 판별하여 DB에 반영한다.
 */
@Service
@RequiredArgsConstructor
public class DeployJudgeScheduler {
    private final StringRedisTemplate srt;
    private final FirmwareDeploymentRepository firmwareDeploymentRepository;
    private final FirmwareDeploymentDeviceRepository firmwareDeploymentDeviceRepository;
    private final OverallDeploymentStatusRepository overallDeploymentStatusRepository;
    private final DeploymentRedisService deploymentRedisService;
    private final EntityManager em;
    private final QuestDbRepository questDbRepository;

    private static final int SCAN_COUNT = 200;

    /**
     * 30초마다 실행되며, Redis에 저장된 배포 commandId들을 스캔한다.
     */
    @Scheduled(fixedDelay = 600000)
    public void dumpDeploySets() {
        System.out.println("=== Redis Dump Start ===");

        var cf = Objects.requireNonNull(srt.getConnectionFactory());
        try (RedisConnection conn = cf.getConnection();
             Cursor<byte[]> cursor = conn.scan(ScanOptions.scanOptions().match("*").count(SCAN_COUNT).build())) {
            while (cursor.hasNext()) {
                String commandId = new String(cursor.next(), StandardCharsets.UTF_8);

                try {
                    List<Long> deviceIds = deploymentRedisService.getAllDeviceIdsFromRedisById(commandId);
                    judge(commandId, deviceIds);
                } catch (Exception e) {
                    System.out.printf("[KEY ERROR] commandId=%s, err=%s%n", commandId, e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("[SCAN ERROR] " + e.getMessage());
        }

        System.out.println("=== Redis Dump End ===");
    }

    /**
     * 주어진 commandId와 디바이스 리스트에 대해 배포 상태를 판별한다.
     * 1. QuestDB에서 최신 다운로드 이벤트를 가져와 완료된 디바이스 처리
     * 2. Redis에 남은 디바이스가 없으면 전체 배포 완료 처리
     * 3. 만료 시간이 지나면 타임아웃 처리
     *
     * @param commandId 배포 식별자
     * @param deviceIds 해당 배포에 포함된 디바이스 ID 리스트
     */
    @Transactional
    public void judge(String commandId, List<Long> deviceIds) {
        FirmwareDeployment firmwareDeployment = firmwareDeploymentRepository.findByCommandIdOrElseThrow(commandId);
        OffsetDateTime expiresAt = firmwareDeployment.getExpiresAt();
        System.out.printf("[SET] commandId=%s, devices=%s, expires=%s%n", commandId, deviceIds, expiresAt);

        List<FirmwareDownloadEvents> downloadEvents = questDbRepository.findLatestPerDeviceByCommandIdAndDeviceIds(commandId, deviceIds);
        List<FirmwareDownloadEvents> completedEvents = downloadEvents.stream()
                .filter(e -> isCompleted(e.getStatus()))
                .toList();

        if (!completedEvents.isEmpty()) {
            processCompletedEvents(commandId, completedEvents, firmwareDeployment);
        }

        Long size = srt.opsForSet().size(commandId);
        if (size == null || size == 0) {
            overallDeploymentStatusRepository.save(new OverallDeploymentStatus(firmwareDeployment, OverallStatus.COMPLETED));
            return;
        }

        if (Instant.now().isAfter(expiresAt.toInstant())) {
            processTimeoutEvents(commandId, firmwareDeployment);
            overallDeploymentStatusRepository.save(new OverallDeploymentStatus(firmwareDeployment, OverallStatus.COMPLETED));
        }
    }

    /**
     * 만료된 배포에 대해 타임아웃 상태로 저장하고 Redis 키를 제거한다.
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
