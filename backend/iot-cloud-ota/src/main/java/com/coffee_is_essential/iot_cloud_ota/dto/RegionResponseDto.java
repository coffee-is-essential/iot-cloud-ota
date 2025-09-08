package com.coffee_is_essential.iot_cloud_ota.dto;

import com.coffee_is_essential.iot_cloud_ota.entity.Region;

public record RegionResponseDto(
        Long regionId,
        String regionCode,
        String regionName
) {
    public static RegionResponseDto from(Region r) {

        return new RegionResponseDto(r.getId(), r.getRegionCode(), r.getRegionName());
    }
}
