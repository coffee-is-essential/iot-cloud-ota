package com.coffee_is_essential.iot_cloud_ota.repository;

import com.coffee_is_essential.iot_cloud_ota.entity.FirmwareDeploymentDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FirmwareDeploymentDeviceRepository extends JpaRepository<FirmwareDeploymentDevice, Long> {
    @Query(value = "SELECT * FROM (" +
                   "  SELECT *, ROW_NUMBER() OVER(PARTITION BY device_id ORDER BY created_at DESC) AS rn " +
                   "  FROM firmware_deployment_device " +
                   "  WHERE deployment_id = :deploymentId" +
                   ") AS ranked_devices " +
                   "WHERE rn = 1",
            nativeQuery = true)
    List<FirmwareDeploymentDevice> findLatestByDeploymentId(@Param("deploymentId") Long deploymentId);
}
