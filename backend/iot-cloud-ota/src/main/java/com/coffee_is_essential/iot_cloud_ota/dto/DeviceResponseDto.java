package com.coffee_is_essential.iot_cloud_ota.dto;

import java.time.OffsetDateTime;

public record DeviceResponseDto(
        Long deviceId,
        String deviceName,
        OffsetDateTime createdAt,
        RegionResponseDto region,
        DivisionResponseDto group,
        OffsetDateTime lastActive
) {
}
