package com.coffee_is_essential.iot_cloud_ota.dto;

import com.coffee_is_essential.iot_cloud_ota.enums.DeploymentType;

import java.util.List;

/**
 * 광고 배포 요청 시, 대상 디바이스를 지정하기 위한 요청 DTO입니다.
 * 광고 ID, 그룹 ID, 지역 ID, 디바이스 ID를 각각 리스트로 받아 필터링된 대상에게 광고를 배포하는 데 사용됩니다.
 *
 * @param adIds          개별 광고 ID 목록
 * @param deploymentType 배포 유형 (예: ALL, GROUP, REGION, DEVICE)
 * @param regions        디바이스 지역 ID 목록
 * @param groups         디바이스 그룹 ID 목록
 * @param devices        개별 디바이스 ID 목록
 */
public record AdsDeploymentRequestDto(
        List<Long> adIds,
        DeploymentType deploymentType,
        List<Long> regions,
        List<Long> groups,
        List<Long> devices
) {
}
