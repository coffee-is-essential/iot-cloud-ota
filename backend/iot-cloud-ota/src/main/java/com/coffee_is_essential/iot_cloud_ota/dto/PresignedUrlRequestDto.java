package com.coffee_is_essential.iot_cloud_ota.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * S3 Presigned URL 요청 DTO 입니다.
 * 클라이언트가 업로드할 펌웨어 파일의 버전과 파일명을 전달합니다.
 *
 * @param version  업로드할 펌웨어의 버전 (예: "v1.0.0")
 * @param filename 업로드할 파일 이름 (예: "firmware.zip)
 */
public record PresignedUrlRequestDto(
        @NotBlank(message = "버전은 비어있을 수 없습니다.")
        String version,
        @NotBlank(message = "파일명은 비어있을 수 없습니다.")
        String filename
) {
}
