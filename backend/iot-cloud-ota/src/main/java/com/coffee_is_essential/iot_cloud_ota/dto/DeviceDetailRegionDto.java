package com.coffee_is_essential.iot_cloud_ota.dto;

import com.coffee_is_essential.iot_cloud_ota.entity.Region;

public record DeviceDetailRegionDto(
        Long id,
        String code,
        String name
) {
    public static DeviceDetailRegionDto from(Region region) {

        return new DeviceDetailRegionDto(region.getId(), region.getRegionCode(), region.getRegionName());
    }
}
