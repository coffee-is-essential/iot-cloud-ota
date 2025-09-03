package com.coffee_is_essential.iot_cloud_ota.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 광고 업로드 Presigned URL 생성을 요청하기 위한 DTO입니다.
 *
 * @param title 광고 제목 (비어있을 수 없음)
 */
public record AdsPresignedUrlRequestDto(
        @NotBlank(message = "광고 제목은 비어있을 수 없습니다.")
        String title
) {
}
