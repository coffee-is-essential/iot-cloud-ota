package com.coffee_is_essential.iot_cloud_ota.dto;

import com.coffee_is_essential.iot_cloud_ota.entity.Region;
import org.springframework.data.domain.Page;

import java.util.List;

public record RegionListResponseDto(
        List<RegionResponseDto> items,
        PaginationMetadataDto paginationMeta
) {
    public static RegionListResponseDto from(Page<Region> regionPage) {

        return new RegionListResponseDto(
                regionPage.getContent()
                        .stream()
                        .map(RegionResponseDto::from)
                        .toList(),
                PaginationMetadataDto.from(regionPage)
        );
    }
}
