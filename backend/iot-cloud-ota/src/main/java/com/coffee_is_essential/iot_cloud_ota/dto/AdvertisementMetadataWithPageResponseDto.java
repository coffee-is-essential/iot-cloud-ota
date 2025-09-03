package com.coffee_is_essential.iot_cloud_ota.dto;

import java.util.List;

/**
 * 페이지네이션된 광고 메타데이터 목록 응답 DTO입니다.
 * 광고 메타데이터 리스트와 함께 페이지네이션 관련 메타데이터를 포함합니다.
 *
 * @param items          광고 메타데이터 응답 DTO 리스트
 * @param paginationMeta 페이지네이션 메타데이터 (현재 페이지, 전체 페이지 수, 아이템 개수 등)
 */
public record AdvertisementMetadataWithPageResponseDto(
        List<AdvertisementMetadataResponseDto> items,
        PaginationMetadataDto paginationMeta
) {
}
