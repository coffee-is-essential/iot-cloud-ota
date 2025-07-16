package com.coffee_is_essential.iot_cloud_ota.repository;

import com.coffee_is_essential.iot_cloud_ota.domain.DeployTargetDeviceInfo;

import java.util.List;

public interface DeviceJpaRepositoryCustom {
    List<DeployTargetDeviceInfo> findByFilterDynamic(List<Long> deviceIds, List<Long> groupIds, List<Long> regionIds);
}
