package com.coffee_is_essential.iot_cloud_ota.service;

import com.coffee_is_essential.iot_cloud_ota.dto.DivisionResponseDto;
import com.coffee_is_essential.iot_cloud_ota.dto.DivisionSummaryResponseDto;
import com.coffee_is_essential.iot_cloud_ota.repository.DivisionJpaRepository;
import lombok.RequiredArgsConstructor;
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
     * 모든 그룹의 기본 정보를 조회합니다.
     *
     * @return DivisionResponseDto 리스트 (groupId, groupCode, groupName)
     */
    public List<DivisionResponseDto> findAllDivisions() {

        return divisionJpaRepository.findAll().stream()
                .map(DivisionResponseDto::from)
                .toList();
    }
}
