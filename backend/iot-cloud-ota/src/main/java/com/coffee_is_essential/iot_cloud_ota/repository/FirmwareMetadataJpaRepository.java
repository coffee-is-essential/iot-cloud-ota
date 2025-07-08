package com.coffee_is_essential.iot_cloud_ota.repository;

import com.coffee_is_essential.iot_cloud_ota.entity.FirmwareMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 펌웨어 메타데이터를 관리하는 JPA 리포지토리 인터페이스입니다.
 */
public interface FirmwareMetadataJpaRepository extends JpaRepository<FirmwareMetadata, Long> {

    /**
     * 지정한 범위(limit, offset)에 따라 생성시각으로 정렬된 펌웨어 메타데이터 목록을 조회합니다.
     *
     * @param limit  조회할 항목 수
     * @param offset 조회 시작 위치
     * @return 정렬된 펌웨어 메타데이터 목록
     */
    @Query(value = """
            SELECT *
            FROM firmware_metadata
            ORDER BY created_at DESC
            LIMIT :limit
            OFFSET :offset
            """, nativeQuery = true)
    List<FirmwareMetadata> findFirmwareMetadataPage(@Param("limit") int limit, @Param("offset") int offset);

    /**
     * 펌웨어 메타데이터 테이블의 전체 레코드 수를 반환합니다.
     *
     * @return 전체 레코드 수
     */
    @Query(value = """
            SELECT COUNT(*)
            FROM firmware_metadata
            """, nativeQuery = true)
    long countAllFirmwareMetadata();
}
