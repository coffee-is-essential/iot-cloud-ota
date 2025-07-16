package com.coffee_is_essential.iot_cloud_ota.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * OTA 배포 대상 디바이스의 정보를 담은 도메인 클래스 입니다.
 */
@Getter
@AllArgsConstructor
public class DeployTargetDeviceInfo {
    private Long deviceId;
    private Long groupId;
    private Long regionId;
}
