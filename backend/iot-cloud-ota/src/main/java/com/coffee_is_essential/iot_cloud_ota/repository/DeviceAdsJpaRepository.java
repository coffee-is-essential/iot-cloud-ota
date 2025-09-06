package com.coffee_is_essential.iot_cloud_ota.repository;

import com.coffee_is_essential.iot_cloud_ota.entity.AdsMetadata;
import com.coffee_is_essential.iot_cloud_ota.entity.Device;
import com.coffee_is_essential.iot_cloud_ota.entity.DeviceAds;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceAdsJpaRepository extends JpaRepository<DeviceAds, Long> {
    @Query("""
                SELECT da.device.id
                FROM DeviceAds da
                WHERE da.adsMetadata = :adsMetadata
                  AND da.endedAt IS NULL
            """)
    List<Long> findActiveDevicesByAds(@Param("adsMetadata") AdsMetadata adsMetadata);
}

