package com.coffee_is_essential.iot_cloud_ota.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 디바이스 요약 정보를 담는 도메인 클래스 입니다.
 */
@Getter
@AllArgsConstructor
public class DeviceSummary {
    private Long deviceId;
    private String deviceName;
    private String regionName;
    private String groupName;
}
