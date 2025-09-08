package com.coffee_is_essential.iot_cloud_ota.controller;

import com.coffee_is_essential.iot_cloud_ota.domain.PaginationInfo;
import com.coffee_is_essential.iot_cloud_ota.dto.RegionListResponseDto;
import com.coffee_is_essential.iot_cloud_ota.dto.RegionSummaryResponseDto;
import com.coffee_is_essential.iot_cloud_ota.service.RegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 리전 관련 요청을 처리하는 REST 컨트롤러 입니다.
 */
@RestController
@RequestMapping("/api/regions")
@RequiredArgsConstructor
public class RegionController {
    private final RegionService regionService;

    /**
     * 전체 리전의 요약 정보를 조회합니다.
     * 각 리전에는 몇 개의 디바이스가 등록되어 있는지를 포함한 정보가 반환됩니다.
     *
     * @return 리전 ID, 리전 코드, 리전 이름, 등록된 디바이스 수를 포함한 리스트와 HTTP 200 OK 응답
     */
    @GetMapping
    public ResponseEntity<List<RegionSummaryResponseDto>> findRegionSummary() {
        List<RegionSummaryResponseDto> list = regionService.findRegionSummary();

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    /**
     * 전체 리전의 상세 정보를 페이지네이션하여 조회합니다.
     * 각 리전에는 몇 개의 디바이스가 등록되어 있는지를 포함한 정보가 반환됩니다.
     *
     * @param page   조회할 페이지 번호 (기본값: 1)
     * @param limit  페이지당 항목 수 (기본값: 10)
     * @param search 검색어 (선택 사항) - 리전 코드 또는 리전 이름을 기준으로 검색
     * @return 페이징된 리전 상세 정보 목록과 페이지 정보가 포함된 응답 DTO와 HTTP 200 OK 응답
     */
    @GetMapping("/list")
    public ResponseEntity<RegionListResponseDto> findAllRegions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String search
    ) {
        PaginationInfo paginationInfo = new PaginationInfo(page, limit, search);
        RegionListResponseDto responseDto = regionService.findAllRegions(paginationInfo);

        return new ResponseEntity(responseDto, HttpStatus.OK);
    }
}
