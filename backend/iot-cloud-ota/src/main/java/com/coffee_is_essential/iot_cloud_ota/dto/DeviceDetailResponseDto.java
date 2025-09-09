package com.coffee_is_essential.iot_cloud_ota.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record DeviceDetailResponseDto(
        Long deviceId,
        String deviceName,
        OffsetDateTime createdAt,
        OffsetDateTime modifiedAt,
        OffsetDateTime lastActiveAt,
        DeviceDetailRegionDto region,
        DeviceDetailDivisionDto group,
        FirmwareResponseDto firmware,
        List<AdsResponseDto> advertisements
) {
}
