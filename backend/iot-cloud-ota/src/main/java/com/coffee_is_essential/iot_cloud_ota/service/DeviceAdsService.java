package com.coffee_is_essential.iot_cloud_ota.service;

import com.coffee_is_essential.iot_cloud_ota.domain.DeviceSummary;
import com.coffee_is_essential.iot_cloud_ota.entity.AdsMetadata;
import com.coffee_is_essential.iot_cloud_ota.repository.DeviceAdsJpaRepository;
import com.coffee_is_essential.iot_cloud_ota.repository.DeviceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceAdsService {
    private final DeviceAdsJpaRepository deviceAdsJpaRepository;
    private final DeviceJpaRepository deviceJpaRepository;

    /**
     * 특정 광고 메타데이터에 대해 활성화된(endedAt이 null인) 디바이스들의 요약 정보를 조회합니다.
     *
     * @param adsMetadata 광고 메타데이터 엔티티
     * @return 활성화된 디바이스들의 요약 정보 리스트
     */
    public List<DeviceSummary> findActiveDevicesSummaryByAds(AdsMetadata adsMetadata) {
        List<Long> activeLists = deviceAdsJpaRepository.findActiveDevicesByAds(adsMetadata);

        return deviceJpaRepository.findDeviceSummaryByIdIn(activeLists);
    }
}
