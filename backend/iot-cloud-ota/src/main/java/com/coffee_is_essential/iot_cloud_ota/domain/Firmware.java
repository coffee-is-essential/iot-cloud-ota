package com.coffee_is_essential.iot_cloud_ota.domain;

import com.coffee_is_essential.iot_cloud_ota.entity.FirmwareMetadata;

import java.time.LocalDateTime;

public record Firmware(
        Long id,
        String version,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
) {
    public static Firmware from(FirmwareMetadata firmwareMetadata) {
        return new Firmware(
                firmwareMetadata.getId(),
                firmwareMetadata.getVersion(),
                firmwareMetadata.getCreatedAt(),
                firmwareMetadata.getModifiedAt()
        );
    }
}
