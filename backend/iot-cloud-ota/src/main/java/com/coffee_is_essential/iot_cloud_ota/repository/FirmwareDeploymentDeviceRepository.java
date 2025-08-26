package com.coffee_is_essential.iot_cloud_ota.repository;

import com.coffee_is_essential.iot_cloud_ota.domain.DeploymentStatusCount;
import com.coffee_is_essential.iot_cloud_ota.domain.Target;
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

    @Query(value = """
            SELECT
                all_statuses.status,
                COALESCE(counted_devices.count, 0) AS count
            FROM (
                     SELECT 'IN_PROGRESS' AS status
                     UNION ALL SELECT 'SUCCEED'
                     UNION ALL SELECT 'FAILED'
                     UNION ALL SELECT 'TIMEOUT'
                 ) AS all_statuses
                     LEFT JOIN (
                SELECT
                    ranked_devices.deployment_status,
                    count(ranked_devices.deployment_status) AS count
                FROM
                    (SELECT
                         *,
                         ROW_NUMBER() OVER (PARTITION BY device_id ORDER BY created_at DESC) AS rn
                     FROM
                         firmware_deployment_device
                     WHERE
                         deployment_id = ?) AS ranked_devices
                WHERE
                    ranked_devices.rn = 1
                GROUP BY
                    ranked_devices.deployment_status
            ) AS counted_devices ON all_statuses.status = counted_devices.deployment_status
            ORDER BY
                all_statuses.status;
            """,
            nativeQuery = true)
    List<DeploymentStatusCount> countStatusByLatestDeployment(@Param("deploymentId") Long deploymentId);

    @Query(value = """
            SELECT id, division_name AS name
            FROM division
            WHERE division.id IN (
                SELECT DISTINCT d.division_id
                FROM firmware_deployment_device fd
                         JOIN device d ON fd.device_id = d.id
                WHERE fd.deployment_id = :deploymentId
            )
            """,
            nativeQuery = true)
    List<Target> findDivisionInfoByDeploymentId(@Param("deploymentId") Long deploymentId);

    @Query(value = """
            SELECT id, region_name AS name
            FROM region
            WHERE region.id IN (
                SELECT DISTINCT d.region_id
                FROM firmware_deployment_device fd
                         JOIN device d ON fd.device_id = d.id
                WHERE fd.deployment_id = :deploymentId)
            """,
            nativeQuery = true)
    List<Target> findRegionInfoByDeploymentId(@Param("deploymentId") Long deploymentId);

    @Query(value = """
            SELECT DISTINCT d.id, d.name
            FROM firmware_deployment_device fd
                     JOIN device d ON fd.device_id = d.id
            WHERE fd.deployment_id = :deploymentId
            """,
            nativeQuery = true)
    List<Target> findDeviceInfoByDeploymentId(@Param("deploymentId") Long deploymentId);
}
