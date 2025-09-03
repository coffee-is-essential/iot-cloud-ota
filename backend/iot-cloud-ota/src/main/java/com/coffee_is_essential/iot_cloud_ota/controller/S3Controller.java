package com.coffee_is_essential.iot_cloud_ota.controller;

import com.coffee_is_essential.iot_cloud_ota.dto.*;
import com.coffee_is_essential.iot_cloud_ota.service.S3Service;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AWS S3 관련 요청을 처리하는 REST 컨트롤러 입니다.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/s3")
public class S3Controller {
    private final S3Service s3Service;

    /**
     * Presigned URL을 발급하여 클라이언트가 인증 없이 S3에 펌웨어 파일을 업로드할 수 있도록 합니다.
     *
     * @param requestDto 업로드할 버전 및 파일명을 포함한 요청 DTO
     * @return 업로드 가능한 S3 Presigned URL 및 실제 저장 경로가 포함된 응답 DTO
     */
    @PostMapping("/presigned_upload")
    public ResponseEntity<UploadPresignedUrlResponseDto> getPresignedUploadUrl(@Valid @RequestBody PresignedUrlRequestDto requestDto) {
        UploadPresignedUrlResponseDto responseDto = s3Service.getPresignedUploadUrl(requestDto.version(), requestDto.fileName());

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    /**
     * 주어진 버전과 파일 이름에 해당하는 펌웨어 파일의 S3 Presigned 다운로드 URL을 반환합니다.
     *
     * @param version  다운로드할 펌웨어의 버전 (예: "v1.0.0")
     * @param fileName 다운로드할 펌웨어의 파일 이름 (예: "firmware.bin")
     * @return 다운로드 가능한 S3 Presigned URL을 담은 응답 DTO
     */
    @GetMapping("/presigned_download")
    public ResponseEntity<DownloadPresignedUrlResponseDto> getPresignedDownloadUrl(
            @RequestParam(required = true) String version,
            @RequestParam(required = true) String fileName
    ) {
        DownloadPresignedUrlResponseDto responseDto = s3Service.getPresignedDownloadUrl(version, fileName);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    /**
     * 광고 업로드를 위한 S3 Presigned URL을 생성합니다.
     *
     * @param requestDto 업로드할 광고의 제목 정보를 담은 요청 DTO
     * @return 업로드용 Presigned URL을 포함한 응답 DTO
     */
    @PostMapping("/ads/presigned_upload")
    public ResponseEntity<AdsUploadPresignedUrlResponseDto> getAdsPresignedUploadUrl(@Valid @RequestBody AdsPresignedUrlRequestDto requestDto) {
        AdsUploadPresignedUrlResponseDto responseDto = s3Service.getAdsPresignedUploadUrl(requestDto.title());

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    /**
     * 광고 파일 다운로드를 위한 S3 Presigned URL을 반환합니다.
     *
     * @param title 광고 제목
     * @return Presigned URL을 담은 응답 DTO
     */
    @GetMapping("/ads/presigned_download")
    public ResponseEntity<DownloadPresignedUrlResponseDto> getPresignedDownloadUrl(
            @RequestParam(required = true) String title
    ) {
        DownloadPresignedUrlResponseDto responseDto = s3Service.getAdsPresignedDownloadUrl(title);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
