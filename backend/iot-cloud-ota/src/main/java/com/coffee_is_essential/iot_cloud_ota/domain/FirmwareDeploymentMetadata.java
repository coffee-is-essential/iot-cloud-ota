package com.coffee_is_essential.iot_cloud_ota.domain;

import com.coffee_is_essential.iot_cloud_ota.enums.OverallDeploymentStatus;

import java.time.LocalDateTime;
import java.util.List;

public record FirmwareDeploymentMetadata(
        Long id,
        Firmware firmware,
        List<Target> targets,
        Long totalDevices,
        Long successCount,
        Long inProgressCount,
        Long inFailedCount,
        OverallDeploymentStatus status,
        LocalDateTime deployedAt,
        LocalDateTime expiresAt
) {
}
