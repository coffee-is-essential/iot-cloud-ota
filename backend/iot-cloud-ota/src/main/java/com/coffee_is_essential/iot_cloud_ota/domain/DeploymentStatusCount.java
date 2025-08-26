package com.coffee_is_essential.iot_cloud_ota.domain;

/**
 * 배포 상태별 장치 개수를 나타내는 DTO
 *
 * @param deploymentStatus 배포 상태 (예: IN_PROGRESS, SUCCEED, FAILED, TIMEOUT)
 * @param count            해당 상태에 속한 장치 수
 */
public record DeploymentStatusCount(
        String deploymentStatus,
        Long count
) {
}
