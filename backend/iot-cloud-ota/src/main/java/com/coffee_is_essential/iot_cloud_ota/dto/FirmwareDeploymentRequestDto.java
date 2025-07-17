package com.coffee_is_essential.iot_cloud_ota.dto;

import java.util.List;

/**
 * 펌웨어 배포 요청 시, 대상 디바이스를 지정하기 위한 요청 DTO입니다.
 * 디바이스 ID, 그룹 ID, 지역 ID를 각각 리스트로 받아 필터링된 대상에게 펌웨어를 배포하는 데 사용됩니다.
 *
 * @param deviceIds 개별 디바이스 ID 목록
 * @param groupIds  디바이스 그룹 ID 목록
 * @param regionIds 디바이스 지역 ID 목록
 */
public record FirmwareDeploymentRequestDto(
        List<Long> deviceIds,
        List<Long> groupIds,
        List<Long> regionIds
) {
}
