package com.coffee_is_essential.iot_cloud_ota.service;

import com.coffee_is_essential.iot_cloud_ota.dto.RegionSummaryResponseDto;
import com.coffee_is_essential.iot_cloud_ota.repository.RegionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegionService {
    private final RegionJpaRepository regionJpaRepository;

    /**
     * 각 리전에 등록된 디바이스 수 요약 정보를 조회합니다.
     *
     * @return RegionSummaryResponseDto 리스트 (regionId, regionCode, regionName, count 포함)
     */
    public List<RegionSummaryResponseDto> findRegionSummary() {

        return regionJpaRepository.findRegionSummary()
                .stream()
                .map(RegionSummaryResponseDto::from)
                .toList();
    }
}
