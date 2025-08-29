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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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

    @Scheduled(fixedDelay = 30_000)
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

    @Transactional
    public void judge(String commandId, List<Long> deviceIds) {
        FirmwareDeployment firmwareDeployment = firmwareDeploymentRepository.findByCommandIdOrElseThrow(commandId);
        LocalDateTime expiresAt = firmwareDeployment.getExpiresAt();
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

        if (LocalDateTime.now().isAfter(expiresAt)) {
            processExpireEvents(commandId, firmwareDeployment);
            overallDeploymentStatusRepository.save(new OverallDeploymentStatus(firmwareDeployment, OverallStatus.COMPLETED));
        }
    }


    private void processExpireEvents(String commandId, FirmwareDeployment deployment) {
        List<FirmwareDeploymentDevice> list = deploymentRedisService.getAllDeviceIdsFromRedisById(commandId).stream()
                .map(e -> new FirmwareDeploymentDevice(
                        em.getReference(Device.class, e),
                        deployment,
                        DeploymentStatus.TIMEOUT
                ))
                .toList();

        firmwareDeploymentDeviceRepository.saveAll(list);
        srt.delete(commandId);
    }

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

    private boolean isCompleted(String deploymentStatus) {
        return deploymentStatus.equals(DeploymentStatus.SUCCESS.name()) || deploymentStatus.equals(DeploymentStatus.FAILED.name())
               || deploymentStatus.equals(DeploymentStatus.CANCELLED.name()) || deploymentStatus.equals(DeploymentStatus.TIMEOUT.name());
    }
}
