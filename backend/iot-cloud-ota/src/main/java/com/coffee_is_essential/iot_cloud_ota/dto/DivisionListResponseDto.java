package com.coffee_is_essential.iot_cloud_ota.dto;

import com.coffee_is_essential.iot_cloud_ota.entity.Division;
import org.springframework.data.domain.Page;

import java.util.List;

public record DivisionListResponseDto(
        List<DivisionResponseDto> divisions,
        PaginationMetadataDto paginationMeta
) {
    public static DivisionListResponseDto from(Page<Division> divisionPage) {
        PaginationMetadataDto metadataDto = new PaginationMetadataDto(
                divisionPage.getPageable().getPageNumber() + 1,
                divisionPage.getPageable().getPageSize(),
                divisionPage.getTotalPages(),
                divisionPage.getTotalElements()
        );

        return new DivisionListResponseDto(
                divisionPage.getContent()
                        .stream()
                        .map(DivisionResponseDto::from)
                        .toList(),
                metadataDto
        );
    }
}
