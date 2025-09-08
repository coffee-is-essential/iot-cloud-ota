package com.coffee_is_essential.iot_cloud_ota.service;

import com.coffee_is_essential.iot_cloud_ota.domain.PaginationInfo;
import com.coffee_is_essential.iot_cloud_ota.dto.RegionListResponseDto;
import com.coffee_is_essential.iot_cloud_ota.dto.RegionResponseDto;
import com.coffee_is_essential.iot_cloud_ota.dto.RegionSummaryResponseDto;
import com.coffee_is_essential.iot_cloud_ota.entity.Region;
import com.coffee_is_essential.iot_cloud_ota.repository.RegionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

        return regionJpaRepository.findRegionSummary().stream()
                .map(RegionSummaryResponseDto::from)
                .toList();
    }

    /**
     * 전체 리전의 상세 정보를 페이지네이션하여 조회합니다.
     * 각 리전에는 몇 개의 디바이스가 등록되어 있는지를 포함한 정보가 반환됩니다.
     *
     * @param paginationInfo 페이지네이션 및 검색어 정보
     * @return 페이징된 리전 상세 정보 목록과 페이지 정보가 포함된 응답 DTO
     */
    public RegionListResponseDto findAllRegions(PaginationInfo paginationInfo) {
        Pageable pageable = PageRequest.of(paginationInfo.page() - 1, paginationInfo.limit(), Sort.by("id").ascending());
        String keyword = paginationInfo.search();

        Page<Region> regionPage = regionJpaRepository.searchWithNullableKeyword(keyword, pageable);

        return RegionListResponseDto.from(regionPage);
    }
}
