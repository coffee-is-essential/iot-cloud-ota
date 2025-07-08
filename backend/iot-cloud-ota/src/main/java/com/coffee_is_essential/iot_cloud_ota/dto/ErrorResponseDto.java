package com.coffee_is_essential.iot_cloud_ota.dto;

import java.util.Map;

/**
 * 에러메시지를 반환하는 DTO 입니다.
 *
 * @param errors errors 필드명과 에러 메시지를 담은 Map
 */
public record ErrorResponseDto(
        Map<String, String> errors
) {
}
