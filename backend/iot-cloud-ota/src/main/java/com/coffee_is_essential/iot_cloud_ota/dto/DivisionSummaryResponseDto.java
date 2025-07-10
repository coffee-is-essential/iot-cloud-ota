package com.coffee_is_essential.iot_cloud_ota.dto;

import com.coffee_is_essential.iot_cloud_ota.domain.DivisionSummary;

/**
 * 그룹의 요약 정보를 클라이언트에 응답하기 위한 DTO 입니다.
 *
 * @param groupId   그룹 ID
 * @param groupCode 그룹 코드 (예: group-a1)
 * @param groupName 그룹 이름 (예: 본사 그룹)
 * @param count     해당 그룹에 등록된 디바이스 수
 */
public record DivisionSummaryResponseDto(
        Long groupId,
        String groupCode,
        String groupName,
        Long count
) {
    public static DivisionSummaryResponseDto from(DivisionSummary divisionSummary) {
        return new DivisionSummaryResponseDto(
                divisionSummary.getDivisionId(),
                divisionSummary.getDivisionCode(),
                divisionSummary.getDivisionName(),
                divisionSummary.getCount()
        );
    }
}
