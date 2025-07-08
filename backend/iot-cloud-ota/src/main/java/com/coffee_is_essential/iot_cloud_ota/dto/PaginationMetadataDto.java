package com.coffee_is_essential.iot_cloud_ota.dto;

/**
 * 페이지네이션에 대한 메타데이터 정보를 포한한 DTO 입니다.
 *
 * @param page       현재 페이지 번호 (1부터 시작)
 * @param limit      페이지당 항목 수
 * @param totalPage  전체 페이지 수
 * @param totalCount 전체 항목 수
 */
public record PaginationMetadataDto(
        int page,
        int limit,
        int totalPage,
        long totalCount
) {
}
