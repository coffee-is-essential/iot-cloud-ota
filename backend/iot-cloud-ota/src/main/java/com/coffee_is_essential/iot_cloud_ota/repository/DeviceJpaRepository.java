package com.coffee_is_essential.iot_cloud_ota.repository;

import com.coffee_is_essential.iot_cloud_ota.domain.DeviceSummary;
import com.coffee_is_essential.iot_cloud_ota.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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

}
