package com.coffee_is_essential.iot_cloud_ota.dto;

import com.coffee_is_essential.iot_cloud_ota.entity.Division;

public record DeviceDetailDivisionDto(
        Long id,
        String code,
        String name
) {
    public static DeviceDetailDivisionDto from(Division division) {
        return new DeviceDetailDivisionDto(division.getId(), division.getDivisionCode(), division.getDivisionName());
    }
}
