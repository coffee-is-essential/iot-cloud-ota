package com.coffee_is_essential.iot_cloud_ota.repository;

import com.coffee_is_essential.iot_cloud_ota.domain.DivisionSummary;
import com.coffee_is_essential.iot_cloud_ota.entity.Division;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

public interface DivisionJpaRepository extends JpaRepository<Division, Long> {
    /**
     * 주어진 ID로 디비전을 조회하고, 존재하지 않을 경우 {@link ResponseStatusException} 예외를 발생시킵니다.
     *
     * @param id 조회할 디비전의 ID
     * @return 존재하는 디비전 엔티티
     */
    default Division findByIdOrElseThrow(Long id) {
        return findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "[ID: " + id + "] 그룹을 찾을 수 없습니다."));
    }

    /**
     * 디바이스 수를 포함한 디비전 요약 목록을 조회합니다.
     * 각 디비전에 연결된 디바이스 개수를 계산하여 반환합니다.
     *
     * @return DivisionSummary 리스트
     */
    @Query(value = """
            SELECT di.id AS divisionId , division_code AS divisionCode, division_name as divisionName, COUNT(*) AS count
            FROM division di
                     JOIN device de ON di.id = de.division_id
            GROUP BY di.id, division_code, division_name
            """, nativeQuery = true)
    List<DivisionSummary> findDivisionSummary();
}
