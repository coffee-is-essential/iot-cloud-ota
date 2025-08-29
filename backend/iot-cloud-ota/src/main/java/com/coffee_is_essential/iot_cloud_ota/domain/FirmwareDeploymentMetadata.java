package com.coffee_is_essential.iot_cloud_ota.domain;

import com.coffee_is_essential.iot_cloud_ota.entity.FirmwareDeployment;
import com.coffee_is_essential.iot_cloud_ota.entity.OverallDeploymentStatus;
import com.coffee_is_essential.iot_cloud_ota.enums.DeploymentStatus;
import com.coffee_is_essential.iot_cloud_ota.enums.DeploymentType;
import com.coffee_is_essential.iot_cloud_ota.enums.OverallStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 펌웨어 배포(FirmwareDeployment)의 메타데이터를 담는 클래스 입니다.
 * 배포 대상, 배포 상태별 카운트, 전체 배포 상태 등을 포함합니다.
 *
 * @param id              배포 ID (FirmwareDeployment의 PK)
 * @param firmware        배포된 펌웨어 메타데이터 정보
 * @param deploymentType  배포 방식 (예: DEVICE, REGION, DIVISION)
 * @param targetInfo      배포 대상 디바이스/그룹 정보 리스트
 * @param totalCount      배포 대상 총 장치 수
 * @param successCount    성공(SUCCEED)한 장치 수
 * @param inProgressCount 진행 중(IN_PROGRESS)인 장치 수
 * @param failedCount     실패(FAILED, TIMEOUT 포함)한 장치 수
 * @param status          전체 배포 상태 (예: 전체 성공, 일부 실패 등)
 * @param deployedAt      배포 시작 시각
 * @param expiresAt       Signed URL 만료 시각
 */
public record FirmwareDeploymentMetadata(
        Long id,
        Firmware firmware,
        DeploymentType deploymentType,
        List<Target> targetInfo,
        Long totalCount,
        Long successCount,
        Long inProgressCount,
        Long failedCount,
        OverallStatus status,
        LocalDateTime deployedAt,
        LocalDateTime expiresAt
) {
    public static FirmwareDeploymentMetadata of(FirmwareDeployment firmwareDeployment, List<Target> targetInfo, ProgressCount progressCount, OverallStatus overallStatus) {

        return new FirmwareDeploymentMetadata(
                firmwareDeployment.getId(),
                Firmware.from(firmwareDeployment.getFirmwareMetadata()),
                firmwareDeployment.getDeploymentType(),
                targetInfo,
                progressCount.getTotalCount(),
                progressCount.getSuccessCount(),
                progressCount.getInProgressCount(),
                progressCount.getFailedCount(),
                overallStatus,
                firmwareDeployment.getDeployedAt(),
                firmwareDeployment.getExpiresAt()
        );
    }
}
