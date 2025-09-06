package com.coffee_is_essential.iot_cloud_ota.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 광고 메타데이터 저장 요청 DTO입니다.
 *
 * @param title          광고 제목
 * @param description    광고 설명
 * @param originalS3Path 원본 파일의 S3 경로
 * @param binaryS3Path   변환된 바이너리 파일의 S3 경로
 */
public record AdsMetadataRequestDto(
        @NotBlank(message = "광고 제목은 비어있을 수 없습니다.")
        String title,
        @NotBlank(message = "설명은 비어있을 수 없습니다.")
        String description,
        @NotBlank(message = "원본 S3 Path는 비어있을 수 없습니다.")
        String originalS3Path,
        @NotBlank(message = "binary S3 Path는 비어있을 수 없습니다.")
        String binaryS3Path
) {
}
