package com.coffee_is_essential.iot_cloud_ota.dto;

import com.coffee_is_essential.iot_cloud_ota.domain.DeployTargetDeviceInfo;
import com.coffee_is_essential.iot_cloud_ota.domain.DeploymentContent;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;
import java.util.List;

public record FirmwareDeploymentDto(
        @JsonProperty("command_id")
        String commandId,
        DeploymentContent content,
        List<DeployTargetDeviceInfo> devices,
        OffsetDateTime timestamp
) {
}
