package com.coffee_is_essential.iot_cloud_ota.dto;

import com.coffee_is_essential.iot_cloud_ota.entity.Division;
import org.springframework.data.domain.Page;

import java.util.List;

public record DivisionListResponseDto(
        List<DivisionResponseDto> items,
        PaginationMetadataDto paginationMeta
) {
    public static DivisionListResponseDto from(Page<Division> divisionPage) {

        return new DivisionListResponseDto(
                divisionPage.getContent()
                        .stream()
                        .map(DivisionResponseDto::from)
                        .toList(),
                PaginationMetadataDto.from(divisionPage)
        );
    }
}
