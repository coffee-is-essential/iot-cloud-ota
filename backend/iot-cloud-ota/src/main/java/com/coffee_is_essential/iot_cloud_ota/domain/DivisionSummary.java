package com.coffee_is_essential.iot_cloud_ota.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 그룹의 요약 정보를 나타내는 도메인 클래스입니다.
 * 각 그룹에 속한 디바이스 수를 포함한 집계 정보를 저장합니다
 */
@Getter
@AllArgsConstructor
public class DivisionSummary {
    private Long divisionId;
    private String divisionCode;
    private String divisionName;
    private Long count;
}
