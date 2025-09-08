package com.coffee_is_essential.iot_cloud_ota.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class SystemStatus {
    private Long deviceId;
    private double cpuCore0;
    private double cpuCore1;
    private double memoryUsage;
    private double storageUsage;
    private Long uptime;
    private OffsetDateTime timestamp;
}
