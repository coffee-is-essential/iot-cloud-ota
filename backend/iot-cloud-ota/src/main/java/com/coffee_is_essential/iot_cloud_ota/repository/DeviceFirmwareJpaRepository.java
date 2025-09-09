package com.coffee_is_essential.iot_cloud_ota.repository;

import com.coffee_is_essential.iot_cloud_ota.entity.DeviceFirmware;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeviceFirmwareJpaRepository extends JpaRepository<DeviceFirmware, Long> {
    Optional<DeviceFirmware> findByDeviceIdAndEndedAtIsNull(Long deviceId);
}
