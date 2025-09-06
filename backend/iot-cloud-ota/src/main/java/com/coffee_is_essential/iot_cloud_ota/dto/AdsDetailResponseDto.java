package com.coffee_is_essential.iot_cloud_ota.dto;

import com.coffee_is_essential.iot_cloud_ota.entity.ActiveDeviceInfo;

import java.util.List;

public record AdsDetailResponseDto(
        AdsMetadataResponseDto adsMetadata,
        List<ActiveDeviceInfo> devices
) {
}
