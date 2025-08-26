package com.coffee_is_essential.iot_cloud_ota.domain;

import com.coffee_is_essential.iot_cloud_ota.enums.DeploymentType;

public record Target(
        Long id,
        DeploymentType type,
        String name
) {
}
