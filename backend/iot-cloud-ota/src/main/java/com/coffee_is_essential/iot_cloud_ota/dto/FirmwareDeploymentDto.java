package com.coffee_is_essential.iot_cloud_ota.dto;

import com.coffee_is_essential.iot_cloud_ota.domain.FirmwareDeployInfo;
import com.coffee_is_essential.iot_cloud_ota.domain.DeployTargetDeviceInfo;

import java.util.List;

/**
 * 펌웨어 배포를 위한 정보들을 포함하는 DTO 입니다.
 *
 * @param signedUrl CloudFront로부터 발급된 Signed URL
 * @param fileInfo  펌웨어 배포 관련 메타데이터 (버전, 크기, 해시 등)
 * @param devices   실제 배포 대상이 되는 디바이스 정보 목록
 */
public record FirmwareDeploymentDto(
        String signedUrl,
        FirmwareDeployInfo fileInfo,
        List<DeployTargetDeviceInfo> devices
) {
}
