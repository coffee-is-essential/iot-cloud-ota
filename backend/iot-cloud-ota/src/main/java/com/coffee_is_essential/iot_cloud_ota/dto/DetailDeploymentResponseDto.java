package com.coffee_is_essential.iot_cloud_ota.dto;

import com.coffee_is_essential.iot_cloud_ota.domain.DeploymentStatusCount;
import com.coffee_is_essential.iot_cloud_ota.domain.DeviceDeploymentStatus;
import com.coffee_is_essential.iot_cloud_ota.domain.Firmware;
import com.coffee_is_essential.iot_cloud_ota.domain.Target;
import com.coffee_is_essential.iot_cloud_ota.entity.FirmwareDeployment;
import com.coffee_is_essential.iot_cloud_ota.entity.OverallDeploymentStatus;
import com.coffee_is_essential.iot_cloud_ota.enums.DeploymentStatus;
import com.coffee_is_essential.iot_cloud_ota.enums.DeploymentType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 펌웨어 배포 상세 정보를 담는 응답 DTO.
 * 배포 메타데이터, 대상 장치 정보, 배포 상태 집계,
 * 디바이스별 상세 배포 이력을 포함합니다.
 *
 * @param id              배포 고유 식별자
 * @param commandId       배포 명령 ID
 * @param firmware        배포되는 펌웨어 메타데이터
 * @param deploymentType  배포 타입
 * @param targetInfo      배포 대상 정보 (Division, Region, Device 등)
 * @param totalCount      배포 대상 디바이스 총 개수
 * @param successCount    성공적으로 배포 완료된 디바이스 개수
 * @param inProgressCount 현재 배포 진행 중인 디바이스 개수
 * @param failedCount     실패(TIMEOUT 포함)한 디바이스 개수
 * @param status          전체 배포 상태
 * @param deployedAt      배포 시작 시각
 * @param expiresAt       배포 만료 시각
 * @param devices         디바이스별 상세 배포 상태 목록
 */
public record DetailDeploymentResponseDto(
        Long id,
        String commandId,
        Firmware firmware,
        DeploymentType deploymentType,
        List<Target> targetInfo,
        Long totalCount,
        Long successCount,
        Long inProgressCount,
        Long failedCount,
        DeploymentStatus status,
        LocalDateTime deployedAt,
        LocalDateTime expiresAt,
        List<DeviceDeploymentStatus> devices
) {
    public static DetailDeploymentResponseDto of(FirmwareDeployment firmwareDeployment, List<Target> targetInfo, List<DeviceDeploymentStatus> devices, List<DeploymentStatusCount> countList, OverallDeploymentStatus status) {
        long total = 0, success = 0, inProgress = 0, failed = 0;
        for (DeploymentStatusCount statusCount : countList) {
            if (statusCount.deploymentStatus().equals(DeploymentStatus.IN_PROGRESS.name())) {
                inProgress += statusCount.count();
            } else if (statusCount.deploymentStatus().equals(DeploymentStatus.TIMEOUT.name())) {
                failed += statusCount.count();
            } else if (statusCount.deploymentStatus().equals(DeploymentStatus.SUCCEED.name())) {
                success += statusCount.count();
            } else if (statusCount.deploymentStatus().equals(DeploymentStatus.FAILED.name())) {
                failed += statusCount.count();
            }

            total += statusCount.count();
        }
        return new DetailDeploymentResponseDto(
                firmwareDeployment.getId(),
                firmwareDeployment.getCommandId(),
                Firmware.from(firmwareDeployment.getFirmwareMetadata()),
                firmwareDeployment.getDeploymentType(),
                targetInfo,
                total,
                success,
                inProgress,
                failed,
                status.getDeploymentStatus(),
                firmwareDeployment.getDeployedAt(),
                firmwareDeployment.getExpiresAt(),
                devices
        );
    }
}
