package com.coffee_is_essential.iot_cloud_ota.repository;

import com.coffee_is_essential.iot_cloud_ota.entity.FirmwareMetadata;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

/**
 * 펌웨어 메타데이터를 관리하는 JPA 리포지토리 인터페이스입니다.
 */
public interface FirmwareMetadataJpaRepository extends JpaRepository<FirmwareMetadata, Long> {
    /**
     * 주어진 ID에 해당하는 펌웨어 메타데이터를 조회합니다.
     * 존재하지 않을 경우 404 NOT_FOUND 예외를 발생시킵니다.
     *
     * @param id 조회할 펌웨어 메타데이터의 고유 ID
     * @return 조회된 {@link FirmwareMetadata} 엔티티
     */
    default FirmwareMetadata findByIdOrElseThrow(Long id) {
        return findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "[ID: " + id + "] 펌웨어를 찾을 수 없습니다."));
    }

    /**
     * 주어진 버전과 파일 이름에 해당하는 펌웨어 메타데이터를 반환합니다.
     *
     * @param version  펌에어 버전
     * @param fileName 펌웨어 파일 이름
     * @return 존재하면 Optional로 감싼 결과, 존재하지 않으면 Optional.empty()
     */
    Optional<FirmwareMetadata> findByVersionAndFileName(String version, String fileName);

    /**
     * 주어진 버전과 파일 이름에 해당하는 펌웨어 메타데이터를 조회합니다.
     * 존재하지 않을 경우 404 NOT_FOUND 예외를 발생시킵니다.
     *
     * @param version  펌웨어 버전
     * @param fileName 펌웨어 파일 이름
     * @return 조회된 {@link FirmwareMetadata} 엔티티
     */
    default FirmwareMetadata findByVersionAndFileNameOrElseThrow(String version, String fileName) {
        return findByVersionAndFileName(version, fileName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "[Version: " + version + ", FileName: " + fileName + "] 펌웨어를 찾을 수 없습니다."));
    }

    /**
     * 지정된 S3 경로에 해당하는 펌웨어 메타데이터가 이미 존재하는지 확인합니다.
     *
     * @param s3Path 확인할 S3 경로
     * @return 해당 경로에 펌웨어가 존재하면 true, 없으면 false
     */
    boolean existsByS3Path(String s3Path);


    /**
     * 키워드가 null이거나 빈 문자열일 경우 전체 데이터를 조회하고,
     * 그렇지 않으면 version, fileName, releaseNote에 키워드가 포함된 레코드를 검색합니다.
     *
     * @param keyword  검색 키워드 (nullable). null 또는 빈 문자열이면 전체 조회.
     * @param pageable 페이징 정보 (페이지 번호, 크기, 정렬 기준 등)
     * @return 키워드가 포함된 결과 혹은 전체 결과의 Page 객체
     */
    @Query("""
            SELECT f
            FROM FirmwareMetadata f
            WHERE (:keyword IS NULL OR :keyword = '' OR
                   f.version LIKE %:keyword% OR
                   f.fileName LIKE %:keyword% OR
                   f.releaseNote LIKE %:keyword%)
            """)
    Page<FirmwareMetadata> searchWithNullableKeyword(@Param("keyword") String keyword, Pageable pageable);
}
