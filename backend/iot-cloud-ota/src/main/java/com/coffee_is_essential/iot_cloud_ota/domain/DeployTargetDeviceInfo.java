package com.coffee_is_essential.iot_cloud_ota.domain;


import com.coffee_is_essential.iot_cloud_ota.entity.Device;

/**
 * OTA 배포 대상 디바이스의 정보를 담은 도메인 클래스 입니다.
 */
public record DeployTargetDeviceInfo(Long deviceId) {

    public static DeployTargetDeviceInfo from(Device device) {

        return new DeployTargetDeviceInfo(device.getDeviceId());
    }
}
