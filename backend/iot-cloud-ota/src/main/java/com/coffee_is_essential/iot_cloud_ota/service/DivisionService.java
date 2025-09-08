package com.coffee_is_essential.iot_cloud_ota.service;

import com.coffee_is_essential.iot_cloud_ota.domain.PaginationInfo;
import com.coffee_is_essential.iot_cloud_ota.dto.DivisionListResponseDto;
import com.coffee_is_essential.iot_cloud_ota.dto.DivisionResponseDto;
import com.coffee_is_essential.iot_cloud_ota.dto.DivisionSummaryResponseDto;
import com.coffee_is_essential.iot_cloud_ota.entity.Division;
import com.coffee_is_essential.iot_cloud_ota.repository.DivisionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DivisionService {
    private final DivisionJpaRepository divisionJpaRepository;

    /**
     * 그룹별 디바이스 개수 요약 정보를 조회합니다.
     *
     * @return DivisionSummaryResponseDto 리스트 (groupId, groupCode, groupName, count)
     */
    public List<DivisionSummaryResponseDto> findDivisionSummary() {

        return divisionJpaRepository.findDivisionSummary()
                .stream()
                .map(DivisionSummaryResponseDto::from)
                .toList();
    }

    /**
     * 전체 그룹의 상세 정보를 페이지네이션하여 조회합니다.
     * 각 그룹에는 몇 개의 디바이스가 등록되어 있는지를 포함한 정보가 반환됩니다.
     *
     * @param paginationInfo 페이지네이션 및 검색어 정보
     * @return 페이징된 그룹 상세 정보 목록과 페이지 정보가 포함된 응답 DTO
     */
    public DivisionListResponseDto findAllDivisions(PaginationInfo paginationInfo) {
        Pageable pageable = PageRequest.of(paginationInfo.page() - 1, paginationInfo.limit(), Sort.by("id").ascending());
        String keyword = paginationInfo.search();

        Page<Division> divisionPage = divisionJpaRepository.searchWithNullableKeyword(keyword, pageable);

        return DivisionListResponseDto.from(divisionPage);
    }
}
