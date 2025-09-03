package com.coffee_is_essential.iot_cloud_ota.repository;

import com.coffee_is_essential.iot_cloud_ota.entity.AdvertisementMetadata;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdvertisementMetadataJpaRepository extends JpaRepository<AdvertisementMetadata, Long> {
    Optional<AdvertisementMetadata> findByTitle(String title);

    boolean existsByOriginalS3Path(String originalS3Path);

    @Query("""
            SELECT ads
            FROM AdvertisementMetadata ads
            WHERE (:keyword IS NULL OR :keyword = '' OR
                   ads.title LIKE %:keyword% OR
                   ads.description LIKE %:keyword%)
            """)
    Page<AdvertisementMetadata> searchWithNullableKeyword(@Param("keyword") String keyword, Pageable pageable);
}
