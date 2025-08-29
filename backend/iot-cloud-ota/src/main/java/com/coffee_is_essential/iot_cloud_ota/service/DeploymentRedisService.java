package com.coffee_is_essential.iot_cloud_ota.service;

import com.coffee_is_essential.iot_cloud_ota.domain.DeployTargetDeviceInfo;
import com.coffee_is_essential.iot_cloud_ota.entity.FirmwareDownloadEvents;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeploymentRedisService {
    private final StringRedisTemplate srt;

    public void addDevices(String commandId, List<DeployTargetDeviceInfo> targetDeviceInfos) {
        srt.opsForSet().add(
                commandId,
                targetDeviceInfos.stream()
                        .map(target -> String.valueOf(target.deviceId()))
                        .toArray(String[]::new)
        );
    }

    public void deleteDevices(String commandId, List<FirmwareDownloadEvents> completedEvents) {
        srt.opsForSet().remove(
                commandId,
                completedEvents.stream()
                        .map(e -> String.valueOf(e.getDeviceId()))
                        .toArray()
        );
        System.out.printf("[DELETED] commandId=%s, saved count=%d, removed count=%d%n", commandId, completedEvents.size(), completedEvents.size());
    }

    public List<Long> getAllDeviceIdsFromRedisById(String commandId) {
        List<Long> deviceIds = srt.opsForSet().members(commandId).stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());

        return deviceIds;
    }
}
