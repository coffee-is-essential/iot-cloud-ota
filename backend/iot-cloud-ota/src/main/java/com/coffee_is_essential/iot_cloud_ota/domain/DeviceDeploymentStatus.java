package com.coffee_is_essential.iot_cloud_ota.domain;

import com.coffee_is_essential.iot_cloud_ota.entity.FirmwareDownloadEvents;

import java.sql.Timestamp;

/**
 * 디바이스별 펌웨어 배포 상태를 나타내는 도메인 모델.
 * 펌웨어 다운로드 이벤트(FirmwareDownloadEvents)에서 필요한 정보만 추출하여
 * 디바이스 ID, 상태, 진행률, 최신 타임스탬프를 저장합니다.
 *
 * @param id        디바이스 고유 식별자
 * @param status    배포 상태 (예: IN_PROGRESS, SUCCEED, FAILED, TIMEOUT)
 * @param progress  배포 진행률
 * @param timestamp 상태가 기록된 시점
 */
public record DeviceDeploymentStatus(
        Long id,
        String status,
        Long progress,
        Timestamp timestamp
) {
    public static DeviceDeploymentStatus from(FirmwareDownloadEvents firmwareDownloadEvents) {
        return new DeviceDeploymentStatus(
                firmwareDownloadEvents.getDeviceId(),
                firmwareDownloadEvents.getStatus(),
                firmwareDownloadEvents.getProgress(),
                firmwareDownloadEvents.getTimestamp()
        );
    }
}
