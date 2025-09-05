package com.coffee_is_essential.iot_cloud_ota.controller;


import com.coffee_is_essential.iot_cloud_ota.domain.PaginationInfo;
import com.coffee_is_essential.iot_cloud_ota.dto.AdvertisementMetadataRequestDto;
import com.coffee_is_essential.iot_cloud_ota.dto.AdvertisementMetadataWithPageResponseDto;
import com.coffee_is_essential.iot_cloud_ota.dto.SaveAdvertisementMetadataResponseDto;
import com.coffee_is_essential.iot_cloud_ota.service.AdvertisementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/ads")
public class AdvertisementController {
    private final AdvertisementService advertisementService;

    /**
     * 광고 메타데이터를 저장합니다.
     *
     * @param requestDto 광고 제목, 설명, 원본/바이너리 S3 경로 정보를 담은 요청 DTO
     * @return 저장된 광고 메타데이터 응답 DTO
     */
    @PostMapping("/metadata")
    public ResponseEntity<SaveAdvertisementMetadataResponseDto> saveAdvertisementMetadata(@Valid @RequestBody AdvertisementMetadataRequestDto requestDto) {
        SaveAdvertisementMetadataResponseDto responseDto = advertisementService.saveAdvertisementMetadata(requestDto);

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    /**
     * 광고 메타데이터 목록을 페이지네이션 방식으로 조회합니다.
     * 검색어가 주어지면 해당 제목 또는 설명에 대해 부분 일치 검색을 수행합니다.
     *
     * @param page   조회할 페이지 번호 (기본값: 1)
     * @param limit  한 페이지당 데이터 개수 (기본값: 10)
     * @param search 검색어 (title or description)
     * @return 페이지네이션 정보와 광고 메타데이터 목록을 담은 응답 DTO
     */
    @GetMapping("/metadata")
    public ResponseEntity<AdvertisementMetadataWithPageResponseDto> findAllWithPagination(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String search
    ) {
        PaginationInfo paginationInfo = new PaginationInfo(page, limit, search);
        AdvertisementMetadataWithPageResponseDto responseDto = advertisementService.findAllWithPagination(paginationInfo);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PostMapping("/metadata/{id}/deploy")
    public ResponseEntity<Void> deploy(@PathVariable Long id) {

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
