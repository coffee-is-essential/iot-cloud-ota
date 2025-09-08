package com.coffee_is_essential.iot_cloud_ota.dto;

import com.coffee_is_essential.iot_cloud_ota.domain.DeployTargetDeviceInfo;
import com.coffee_is_essential.iot_cloud_ota.domain.DeploymentContent;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * 광고 배포에 필요한 정보를 담고 있는 DTO입니다.
 *
 * @param commandId 배포 명령 ID
 * @param contents  배포할 광고 콘텐츠 정보 목록 (Signed URL 및 메타데이터 포함)
 * @param devices   배포 대상 디바이스 정보 목록
 * @param timestamp 배포 요청 시각
 */
public record AdsDeploymentDto(
        @JsonProperty("command_id")
        String commandId,
        List<DeploymentContent> contents,
        List<DeployTargetDeviceInfo> devices,
        OffsetDateTime timestamp
) {
}
