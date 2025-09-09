package com.coffee_is_essential.iot_cloud_ota.dto;

import com.coffee_is_essential.iot_cloud_ota.entity.Region;

public record RegionResponseDto(
        Long regionId,
        String regionCode,
        String regionName
) {
    public static RegionResponseDto from(Region region) {

        return new RegionResponseDto(region.getId(), region.getRegionCode(), region.getRegionName());
    }
}
