package com.coffee_is_essential.iot_cloud_ota.dto;

import com.coffee_is_essential.iot_cloud_ota.entity.Division;

public record DivisionResponseDto(
        Long groupId,
        String groupCode,
        String groupName
) {
    public static DivisionResponseDto from(Division division) {
        return new DivisionResponseDto(division.getId(), division.getDivisionCode(), division.getDivisionName());
    }
}
