package com.coffee_is_essential.iot_cloud_ota.dto;

import java.util.List;

/**
 * 페이징된 펌웨어 메타데이터 목록과 페이지 정보가 포함된 응답 DTO 입니다.
 *
 * @param items          조회된 펌웨어 메타데이터 목록
 * @param paginationMeta 현재 페이지 번호, 페이지당 항목 수, 전체 페이지 수, 전체 항목 수를 포함한 DTO
 */
public record FirmwareMetadataWithPageResponseDto(
        List<FirmwareMetadataResponseDto> items,
        PaginationMetadataDto paginationMeta
) {
}
