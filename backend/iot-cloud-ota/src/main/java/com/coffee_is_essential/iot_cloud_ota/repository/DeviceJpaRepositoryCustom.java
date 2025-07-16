package com.coffee_is_essential.iot_cloud_ota.repository;

import com.coffee_is_essential.iot_cloud_ota.domain.DeployTargetDeviceInfo;

import java.util.List;

/**
 * 디바이스에 대한 커스텀 JPA 쿼리 메서드를 정의하는 인터페이스 입니다.
 */
public interface DeviceJpaRepositoryCustom {
    /**
     * 전달받은 deviceId, groupId, regionId 필터 조건 중 하나라도 일치하는 디바이스 정보를 조회합니다.
     *
     * @param deviceIds 조회할 디바이스 ID 리스트
     * @param groupIds  조회할 그룹 ID 리스트
     * @param regionIds 조회할 리전 ID 리스트
     * @return 조건에 해당하는 디바이스들의 DTO 리스트
     */
    List<DeployTargetDeviceInfo> findByFilterDynamic(List<Long> deviceIds, List<Long> groupIds, List<Long> regionIds);
}
