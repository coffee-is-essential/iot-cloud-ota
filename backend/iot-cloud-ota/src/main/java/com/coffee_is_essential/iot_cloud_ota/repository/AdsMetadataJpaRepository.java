package com.coffee_is_essential.iot_cloud_ota.repository;

import com.coffee_is_essential.iot_cloud_ota.entity.AdsMetadata;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Repository
public interface AdsMetadataJpaRepository extends JpaRepository<AdsMetadata, Long> {
    Optional<AdsMetadata> findByTitle(String title);

    boolean existsByOriginalS3Path(String originalS3Path);

    @Query("""
            SELECT ads
            FROM AdsMetadata ads
            WHERE (:keyword IS NULL OR :keyword = '' OR
                   ads.title LIKE %:keyword% OR
                   ads.description LIKE %:keyword%)
            """)
    Page<AdsMetadata> searchWithNullableKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * ID로 광고 메타데이터를 조회하고, 존재하지 않으면 404 예외를 발생시킵니다.
     *
     * @param id 광고 ID
     * @return 조회된 광고 메타데이터
     * @throws ResponseStatusException 광고가 존재하지 않을 경우 404 상태 코드와 함께 예외 발생
     */
    default AdsMetadata findByIdOrElseThrow(Long id) {
        return findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ID '" + id + "'에 해당하는 광고가 존재하지 않습니다."));
    }

    /**
     * 제목으로 광고 메타데이터를 조회하고, 존재하지 않으면 404 예외를 발생시킵니다.
     *
     * @param title 광고 제목
     * @return 조회된 광고 메타데이터
     * @throws ResponseStatusException 광고가 존재하지 않을 경우 404 상태 코드와 함께 예외 발생
     */
    default AdsMetadata findByTitleOrElseThrow(String title) {
        return findByTitle(title).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "제목 '" + title + "'에 해당하는 광고가 존재하지 않습니다."));
    }
}
