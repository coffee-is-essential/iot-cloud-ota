package com.coffee_is_essential.iot_cloud_ota.dto;

public record PaginationMetadataDto(
        int page,
        int limit,
        int totalPage,
        long totalCount
) {
}
