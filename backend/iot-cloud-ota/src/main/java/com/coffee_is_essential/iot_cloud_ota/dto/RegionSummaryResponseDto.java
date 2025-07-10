package com.coffee_is_essential.iot_cloud_ota.dto;

import com.coffee_is_essential.iot_cloud_ota.domain.RegionSummary;

/**
 * 리전 요약 정보를 클라이언트에 응답하기 위한 DTO 입니다.
 *
 * @param regionId   리전 ID
 * @param regionCode 리전 코드 (예: ap-northeast-2)
 * @param regionName 리전 이름 (예: 서울)
 * @param count      해당 리전에 등록된 디바이스 수
 */
public record RegionSummaryResponseDto(
        Long regionId,
        String regionCode,
        String regionName,
        Long count
) {
    public static RegionSummaryResponseDto from(RegionSummary regionSummary) {
        return new RegionSummaryResponseDto(
                regionSummary.getRegionId(),
                regionSummary.getRegionCode(),
                regionSummary.getRegionName(),
                regionSummary.getCount()
        );
    }
}
