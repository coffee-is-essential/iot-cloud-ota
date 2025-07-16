package com.coffee_is_essential.iot_cloud_ota.dto;

import java.util.List;

public record FirmwareDeploymentRequestDto(
        List<Long> deviceIds,
        List<Long> groupIds,
        List<Long> regionIds
) {
}
