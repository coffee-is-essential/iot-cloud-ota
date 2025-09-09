package com.coffee_is_essential.iot_cloud_ota.repository;

import com.coffee_is_essential.iot_cloud_ota.domain.DeviceSummary;
import com.coffee_is_essential.iot_cloud_ota.entity.Device;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

public interface DeviceJpaRepository extends JpaRepository<Device, Long>, DeviceJpaRepositoryCustom {

    /**
     * 디바이스의 요약 정보를 조회합니다.
     * 디바이스 ID, 디바이스 이름, 지역 이름, 그룹 이름을 포함한 정보를 {@link DeviceSummary} 형태로 반환합니다.
     *
     * @return 디바이스 요약 정보 리스트 {@link DeviceSummary}
     */
    @Query(value = """
            SELECT
            de.id AS deviceId,
            de.name AS deviceName,
            r.region_name AS regionName,
            di.division_name AS groupName
            FROM device de
            JOIN region r ON de.region_id = r.id
            JOIN division di ON de.division_id = di.id
            """, nativeQuery = true)
    List<DeviceSummary> findDeviceSummary();

    /**
     * 리전 ID와 그룹 ID에 따라 디바이스를 필터링하여 페이징된 결과를 조회합니다.
     * 리전 ID 또는 그룹 ID가 null인 경우 해당 조건은 무시됩니다.
     *
     * @param regionId 리전 ID (null 가능)
     * @param groupId  그룹 ID (null 가능)
     * @param pageable 페이징 정보
     * @return 필터링된 디바이스의 페이징된 결과
     */
    @Query("""
            SELECT d FROM Device d
            WHERE (:regionId IS NULL OR d.region.id = :regionId)
              AND (:groupId IS NULL OR d.division.id = :groupId)
            """)
    Page<Device> findByRegionAndGroup(@Param("regionId") Long regionId,
                                      @Param("groupId") Long groupId,
                                      Pageable pageable);

    default Device findByIdOrElseThrow(Long id) {
        return findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "[ID: " + id + "] 기기를 찾을 수 없습니다."));
    }
}
