package com.coffee_is_essential.iot_cloud_ota.dto;

import java.util.List;

public record DeviceListResponseDto(
        List<DeviceResponseDto> items,
        PaginationMetadataDto paginationMeta
) {
    public static DeviceListResponseDto of(List<DeviceResponseDto> items, PaginationMetadataDto paginationMeta) {
        return new DeviceListResponseDto(items, paginationMeta);
    }
}
