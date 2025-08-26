package com.coffee_is_essential.iot_cloud_ota.dto;

import com.coffee_is_essential.iot_cloud_ota.domain.FirmwareDeploymentMetadata;

import java.util.List;

public record FirmwareDeploymentListDto(
        List<FirmwareDeploymentMetadata> list
) {
}
