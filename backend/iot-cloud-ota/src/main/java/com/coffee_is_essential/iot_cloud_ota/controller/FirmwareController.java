package com.coffee_is_essential.iot_cloud_ota.controller;

import com.coffee_is_essential.iot_cloud_ota.dto.*;
import com.coffee_is_essential.iot_cloud_ota.service.FirmwareMetadataService;
import com.coffee_is_essential.iot_cloud_ota.service.S3Service;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 펌웨어 관련 요청을 처리하는 REST 컨트롤러입니다.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/firmwares")
public class FirmwareController {
    private final S3Service s3Service;
    private final FirmwareMetadataService firmwareMetadataService;

    /**
     * Presigned URL을 발급하여 클라이언트가 인증 없이 S3에 펌웨어 파일을 업로드할 수 있도록 합니다.
     *
     * @param requestDto 업로드할 버전 및 파일명을 포함한 요청 DTO
     * @return 업로드 가능한 S3 Presigne URL 및 실제 저장 경로가 포함된 응답 DTO
     */
    @GetMapping("/presigned_upload")
    public ResponseEntity<UploadPresignedUrlResponseDto> getPresignedUploadUrl(@Valid @RequestBody PresignedUrlRequestDto requestDto) {
        UploadPresignedUrlResponseDto responseDto = s3Service.getPresignedUploadUrl(requestDto.version(), requestDto.fileName());

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    /**
     * 펌웨어 메타데이터를 저장합니다.
     *
     * @param requestDto 저장할 펌웨어 메타데이터를 포함한 요청 DTO
     * @return 저장된 펌웨어 메타데이터 정보가 포함된 응답 DTO
     */
    @PostMapping("/metadata")
    public ResponseEntity<FirmwareMetadataResponseDto> saveFirmwareMetadata(@Valid @RequestBody FirmwareMetadataRequestDto requestDto) {
        FirmwareMetadataResponseDto responseDto = firmwareMetadataService.saveFirmwareMetadata(requestDto);

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    /**
     * 펌웨어 메타데이터 목록을 페이지네이션하여 조회합니다.
     *
     * @param page   조회할 페이지 번호 (기본값: 1)
     * @param limit  페이지당 항목 수 (기본값: 10)
     * @param search 검색어 (선택 사항) - 펌웨어 버전 또는 릴리즈 노트 내용을 기준으로 검색
     * @return 페이징된 펌웨어 메타데이터 목록과 페이지 정보가 포함된 응답 DTO
     */
    @GetMapping("/metadata")
    public ResponseEntity<FirmwareMetadataWithPageResponseDto> findAllWithPagination(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String search
    ) {
        FirmwareMetadataWithPageResponseDto responseDto = firmwareMetadataService.findAllWithPagination(page, limit, search);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    /**
     * 지정한 펌웨어 ID에 해당하는 펌웨어 메타데이터를 조회합니다.
     *
     * @param id 조회할 펌웨어 메타데이터의 고유 ID
     * @return 조회된 펌웨어 메타데이터 정보가 포함된 응답 DTO
     */
    @GetMapping("/metadata/{id}")
    public ResponseEntity<FirmwareMetadataResponseDto> findById(@PathVariable Long id) {
        FirmwareMetadataResponseDto responseDto = firmwareMetadataService.findById(id);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    /**
     * 지정한 펌웨어 ID에 해당하는 펌웨어의 S3 Presigned 다운로드 URL을 발급합니다.
     *
     * @param id 다운로드할 펌웨어 메타데이터의 고유 ID
     * @return 다운로드 가능한 Presigned URL을 담은 응답 DTO
     */
    @GetMapping("/{id}/presigned_download")
    public ResponseEntity<DownloadPresignedUrlResponseDto> getPresignedDownloadUrl(@PathVariable Long id) {
        DownloadPresignedUrlResponseDto responseDto = s3Service.getPresignedDownloadUrl(id);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
