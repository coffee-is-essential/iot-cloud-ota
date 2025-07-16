package com.coffee_is_essential.iot_cloud_ota.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DeployTargetDeviceInfo {
    private Long deviceId;
    private Long groupId;
    private Long regionId;
}
