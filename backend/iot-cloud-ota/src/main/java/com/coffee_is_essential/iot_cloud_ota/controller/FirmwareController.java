package com.coffee_is_essential.iot_cloud_ota.controller;

import com.coffee_is_essential.iot_cloud_ota.dto.PresignedUrlRequestDto;
import com.coffee_is_essential.iot_cloud_ota.dto.PresignedUrlResponseDto;
import com.coffee_is_essential.iot_cloud_ota.service.S3Service;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/firmwares")
public class FirmwareController {
    private final S3Service s3Service;

    /**
     * Presigned URL 발급을 위한 엔드포인트입니다.
     * 클라이언트는 이 URL을 통해 인증 없이 S3에 펌웨어 파일을 업로드할 수 있습니다.
     *
     * @param requestDto 업로드할 버전 및 파일명을 담은 요청 DTO
     * @return 업로드 가능한 Presigned URL을 담은 응답 DTO
     */
    @PostMapping("/presigned_url")
    public ResponseEntity<PresignedUrlResponseDto> issuePresignedUrl(@Valid @RequestBody PresignedUrlRequestDto requestDto) {
        String url = s3Service.getPresignedUrl(requestDto.version(), requestDto.filename());
        PresignedUrlResponseDto responseDto = new PresignedUrlResponseDto(url);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
