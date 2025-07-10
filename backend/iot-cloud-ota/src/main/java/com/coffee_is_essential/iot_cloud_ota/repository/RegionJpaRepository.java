package com.coffee_is_essential.iot_cloud_ota.repository;

import com.coffee_is_essential.iot_cloud_ota.domain.RegionSummary;
import com.coffee_is_essential.iot_cloud_ota.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

public interface RegionJpaRepository extends JpaRepository<Region, Long> {
    /**
     * 주어진 리전 ID로 리전을 조회하고, 없을 경우 {@link ResponseStatusException} 예외를 발생시킵니다.
     *
     * @param id 조회할 리전의 ID
     * @return 존재하는 리전 엔티티
     */
    default Region findByIdOrElseThrow(Long id) {
        return findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "[ID: " + id + "] 리전을 찾을 수 없습니다."));
    }

    /**
     * 리전별 디바이스 개수를 요약 조회합니다.
     * 네이티브 SQL 쿼리를 사용하여 각 리전에 속한 디바이스의 개수를 집계합니다.
     *
     * @return RegionSummary 리스트 (regionId, regionCode, regionName, count)
     */
    @Query(value = """
            SELECT r.id AS regionId , region_code AS regionCode, region_name as regionName, COUNT(*) AS count
            FROM region r
            JOIN device d ON r.id = d.region_id
            GROUP BY r.id
            """, nativeQuery = true)
    List<RegionSummary> findRegionSummary();
}
