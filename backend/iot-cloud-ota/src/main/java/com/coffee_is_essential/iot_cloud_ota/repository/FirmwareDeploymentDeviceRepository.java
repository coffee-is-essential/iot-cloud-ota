package com.coffee_is_essential.iot_cloud_ota.repository;

import com.coffee_is_essential.iot_cloud_ota.domain.DeploymentStatusCount;
import com.coffee_is_essential.iot_cloud_ota.domain.Target;
import com.coffee_is_essential.iot_cloud_ota.entity.FirmwareDeploymentDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FirmwareDeploymentDeviceRepository extends JpaRepository<FirmwareDeploymentDevice, Long> {
    /**
     * 특정 배포 ID에 대해 디바이스별 최신 상태만 집계하여 상태별(IN_PROGRESS, SUCCESS, FAILED, TIMEOUT)
     * 장치 개수를 반환합니다.
     *
     * @param deploymentId 조회할 배포 ID
     * @return 상태별 장치 개수를 담은 리스트 (DeploymentStatusCount)
     */
    @Query(value = """
            SELECT
                all_statuses.status,
                COALESCE(counted_devices.count, 0) AS count
            FROM (
                     SELECT 'IN_PROGRESS' AS status
                     UNION ALL SELECT 'SUCCESS'
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

    /**
     * 특정 배포 ID에 포함된 모든 디바이스의 Division 정보를 조회합니다.
     *
     * @param deploymentId 조회할 배포 ID
     * @return 배포 대상 Division 정보 리스트 (id, name)
     */
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

    /**
     * 특정 배포 ID에 포함된 모든 디바이스의 Region 정보를 조회합니다.
     *
     * @param deploymentId 조회할 배포 ID
     * @return 배포 대상 Region 정보 리스트 (id, name)
     */
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

    /**
     * 특정 배포 ID에 포함된 모든 디바이스의 기본 정보를 조회합니다.
     *
     * @param deploymentId 조회할 배포 ID
     * @return 배포 대상 Device 정보 리스트 (id, name)
     */
    @Query(value = """
            SELECT DISTINCT d.id, d.name
            FROM firmware_deployment_device fd
                     JOIN device d ON fd.device_id = d.id
            WHERE fd.deployment_id = :deploymentId
            """,
            nativeQuery = true)
    List<Target> findDeviceInfoByDeploymentId(@Param("deploymentId") Long deploymentId);
}
