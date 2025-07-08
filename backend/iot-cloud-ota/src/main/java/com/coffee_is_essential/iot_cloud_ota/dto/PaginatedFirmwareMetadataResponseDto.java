package com.coffee_is_essential.iot_cloud_ota.dto;

import java.util.List;

public record PaginatedFirmwareMetadataResponseDto(
        List<FirmwareMetadataResponseDto> firmwareMetadataResponseDtos,
        PaginationMetadataDto paginationMetadataDto
) {
}
