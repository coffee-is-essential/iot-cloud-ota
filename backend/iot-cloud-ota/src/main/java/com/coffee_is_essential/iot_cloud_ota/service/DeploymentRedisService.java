package com.coffee_is_essential.iot_cloud_ota.service;

import com.coffee_is_essential.iot_cloud_ota.domain.DeployTargetDeviceInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
