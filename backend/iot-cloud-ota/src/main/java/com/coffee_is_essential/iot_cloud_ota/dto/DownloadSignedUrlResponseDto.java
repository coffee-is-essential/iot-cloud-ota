package com.coffee_is_essential.iot_cloud_ota.dto;

/**
 * S3에서 파일을 다운로드하기 위한 서명된 URL을 응답하기 위한 DTO입니다.
 *
 * @param url 서명된 다운로드 URL
 */
public record DownloadSignedUrlResponseDto(
        String url
) {
}
