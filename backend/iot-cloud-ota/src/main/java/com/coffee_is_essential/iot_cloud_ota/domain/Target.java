package com.coffee_is_essential.iot_cloud_ota.domain;

/**
 * 배포 메타데이터(FirmwareDeploymentMetadata) 내 targetInfo 리스트의 원소로 사용됩니다.
 * 단순히 ID와 이름만 담아, 대상의 기본 정보를 제공하는 용도입니다.
 *
 * @param id   대상의 고유 ID (DB의 Device/Division/Region 등과 매핑)
 * @param name 대상 이름 (예: 디바이스명, 그룹명, 리전명 등)
 */
public record Target(
        Long id,
        String name
) {
}
