package com.coffee_is_essential.iot_cloud_ota.repository;

import com.coffee_is_essential.iot_cloud_ota.entity.AdvertisementMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdvertisementMetadataJpaRepository extends JpaRepository<AdvertisementMetadata, Long> {
    Optional<AdvertisementMetadata> findByTitle(String title);

    boolean existsByOriginalS3Path(String originalS3Path);
}
