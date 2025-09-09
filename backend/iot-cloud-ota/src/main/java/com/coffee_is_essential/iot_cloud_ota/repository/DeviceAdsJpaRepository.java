package com.coffee_is_essential.iot_cloud_ota.repository;

import com.coffee_is_essential.iot_cloud_ota.entity.ActiveDeviceInfo;
import com.coffee_is_essential.iot_cloud_ota.entity.DeviceAds;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DeviceAdsJpaRepository extends JpaRepository<DeviceAds, Long> {
    @Query("""
                SELECT new com.coffee_is_essential.iot_cloud_ota.entity.ActiveDeviceInfo(
                    d.id,
                    d.name,
                    r.regionName,
                    di.divisionName,
                    da.startedAt
                )
                FROM DeviceAds da
                JOIN da.device d
                JOIN d.region r
                JOIN d.division di
                WHERE da.adsMetadata.id = :adsId
                  AND da.endedAt IS NULL
            """)
    List<ActiveDeviceInfo> findActiveDevicesByAdsId(@Param("adsId") Long adsId);

    List<DeviceAds> findByDeviceIdAndEndedAtIsNull(Long deviceId);
}

