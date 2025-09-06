package com.coffee_is_essential.iot_cloud_ota.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@AllArgsConstructor
public class ActiveDeviceInfo {
    private Long deviceId;
    private String deviceName;
    private String regionName;
    private String groupName;
    private OffsetDateTime startedAt;
}
