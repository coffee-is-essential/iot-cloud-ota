package com.coffee_is_essential.iot_cloud_ota.entity;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class FirmwareDownloadEvents {
    private String command_id;
    private String message;
    private String status;
    private Long deviceId;
    private Long progress;
    private Long totalBytes;
    private Long downloadBytes;
    private double speedKbps;
    private boolean checksumVerified;
    private Long downloadTime;
    private Timestamp timestamp;
}
