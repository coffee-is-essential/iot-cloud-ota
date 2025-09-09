package com.coffee_is_essential.iot_cloud_ota.dto;

import java.time.OffsetDateTime;

public record AdsResponseDto(
        Long id,
        String title,
        OffsetDateTime deployedAt,
        String originalSignedUrl
) {
}
