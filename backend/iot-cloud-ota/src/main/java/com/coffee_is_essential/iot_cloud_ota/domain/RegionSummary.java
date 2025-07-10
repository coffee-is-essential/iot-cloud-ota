package com.coffee_is_essential.iot_cloud_ota.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 리전과 관련된 요약 정보를 나타내는 도메인 클래스입니다.
 * 리전 ID, 리전 코드, 리전 이름, 해당 리전에 속한 디바이스 개수를 포함합니다.
 */
@Getter
@AllArgsConstructor
public class RegionSummary {
    private Long regionId;
    private String regionCode;
    private String regionName;
    private Long count;
}
