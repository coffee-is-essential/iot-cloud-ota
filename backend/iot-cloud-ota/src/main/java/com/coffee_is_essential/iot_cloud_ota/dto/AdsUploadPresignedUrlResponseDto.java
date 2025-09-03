package com.coffee_is_essential.iot_cloud_ota.dto;

/**
 * 광고 업로드용 Presigned URL 응답 DTO입니다.
 * 원본 파일과 바이너리 파일 각각의 Presigned URL을 제공합니다.
 *
 * @param original 원본 파일 업로드용 Presigned URL
 * @param binary   바이너리 파일 업로드용 Presigned URL
 */
public record AdsUploadPresignedUrlResponseDto(
        UploadPresignedUrlResponseDto original,
        UploadPresignedUrlResponseDto binary
) {
}
