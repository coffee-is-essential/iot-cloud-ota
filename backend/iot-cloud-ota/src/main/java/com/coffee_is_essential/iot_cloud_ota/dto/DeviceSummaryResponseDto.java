package com.coffee_is_essential.iot_cloud_ota.dto;

import com.coffee_is_essential.iot_cloud_ota.domain.DeviceSummary;

/**
 * 디바이스 요약 정보를 담는 응답 DTO 입니다.
 *
 * @param deviceId   디바이스 ID
 * @param deviceName 디바이스 이름
 * @param regionName 리전 이름
 * @param groupName  그룹 이름
 * @param isActive   활성 상태 여부
 */
public record DeviceSummaryResponseDto(
        Long deviceId,
        String deviceName,
        String regionName,
        String groupName,
        boolean isActive
) {
    public static DeviceSummaryResponseDto from(DeviceSummary deviceSummary) {
        return new DeviceSummaryResponseDto(
                deviceSummary.getDeviceId(),
                deviceSummary.getDeviceName(),
                deviceSummary.getRegionName(),
                deviceSummary.getGroupName(),
                true
        );
    }
}
