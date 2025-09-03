package com.coffee_is_essential.iot_cloud_ota.controller;


import com.coffee_is_essential.iot_cloud_ota.dto.AdvertisementMetadataRequestDto;
import com.coffee_is_essential.iot_cloud_ota.dto.AdvertisementMetadataResponseDto;
import com.coffee_is_essential.iot_cloud_ota.service.AdvertisementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<AdvertisementMetadataResponseDto> saveAdvertisementMetadata(@Valid @RequestBody AdvertisementMetadataRequestDto requestDto) {
        AdvertisementMetadataResponseDto responseDto = advertisementService.saveAdvertisementMetadata(requestDto);

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }
}
