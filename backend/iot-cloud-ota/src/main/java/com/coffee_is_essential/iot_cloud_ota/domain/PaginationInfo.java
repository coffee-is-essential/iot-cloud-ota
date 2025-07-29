package com.coffee_is_essential.iot_cloud_ota.domain;

/**
 * 페이징 및 검색 요청 정보를 담는 DTO입니다.
 * 클라이언트가 전달하는 페이지 번호, 페이지 크기, 검색 키워드를 담고 있으며,
 * 이를 기반으로 페이징 처리 및 조건 검색이 수행됩니다.
 *
 * @param page   요청한 페이지 번호
 * @param limit  한 페이지당 보여줄 항목 수
 * @param search 검색어 (nullable 또는 빈 문자열일 수 있음)
 */
public record PaginationInfo(
        int page,
        int limit,
        String search
) {
}
