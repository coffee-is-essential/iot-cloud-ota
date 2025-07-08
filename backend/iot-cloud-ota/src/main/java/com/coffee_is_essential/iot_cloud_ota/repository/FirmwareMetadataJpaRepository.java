package com.coffee_is_essential.iot_cloud_ota.repository;

import com.coffee_is_essential.iot_cloud_ota.entity.FirmwareMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FirmwareMetadataJpaRepository extends JpaRepository<FirmwareMetadata, Long> {
}
