package com.coffee_is_essential.iot_cloud_ota.dto;

/**
 * S3 Presigned 다운로드 URL 응답 DTO 입니다.
 *
 * @param url S3에 GET 요청을 보낼 수 있는 Presigned URL
 */
public record DownloadPresignedUrlResponseDto(
        String url
) {
}
