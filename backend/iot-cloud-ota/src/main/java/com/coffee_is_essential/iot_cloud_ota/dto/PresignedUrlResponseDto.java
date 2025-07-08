package com.coffee_is_essential.iot_cloud_ota.dto;

/**
 * S3 Presigned URL 응답 DTO입니다.
 * 클라이언트가 해당 URL을 사용하여 S3에 직접 업로드할 수 있습니다.
 *
 * @param url S3에 PUT 요청을 보낼 수 있는 Presigned URL
 */
public record PresignedUrlResponseDto(
        String url
) {
}
