package com.coffee_is_essential.iot_cloud_ota.repository;

import com.coffee_is_essential.iot_cloud_ota.domain.DivisionSummary;
import com.coffee_is_essential.iot_cloud_ota.entity.Division;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
            SELECT di.id AS divisionId,
                   di.division_code AS divisionCode,
                   di.division_name AS divisionName,
                   COUNT(de.id) AS count
            FROM division di
                     LEFT JOIN device de ON di.id = de.division_id
            GROUP BY di.id, di.division_code, di.division_name
            ORDER BY di.id;
            """, nativeQuery = true)
    List<DivisionSummary> findDivisionSummary();

    /**
     * 키워드로 디비전을 검색합니다.
     * 키워드가 null이거나 빈 문자열인 경우 모든 디비전을 반환합니다.
     * 그렇지 않으면 divisionCode 또는 divisionName에 키워드가 포함된 디비전을 반환합니다.
     *
     * @param keyword  검색 키워드 (null 또는 빈 문자열 가능)
     * @param pageable 페이지네이션 정보
     * @return 검색된 디비전의 페이지
     */
    @Query("""
            SELECT d
            FROM Division d
            WHERE (:keyword IS NULL OR :keyword = '' OR
                   d.divisionCode LIKE %:keyword% OR
                   d.divisionName LIKE %:keyword%)
            """)
    Page<Division> searchWithNullableKeyword(String keyword, Pageable pageable);
}
