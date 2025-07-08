package com.coffee_is_essential.iot_cloud_ota.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 펌웨어 메타데이터 등록 요청을 위한 DTO 입니다.
 *
 * @param version     펌웨어 버전
 * @param fileName    펌웨어 파일 이름
 * @param releaseNote 릴리즈 노트
 */
public record FirmwareMetadataRequestDto(
        @NotBlank(message = "버전은 비어있을 수 없습니다.")
        String version,
        @NotBlank(message = "파일명은 비어있을 수 없습니다.")
        String fileName,
        @NotBlank(message = "릴리즈 노트는 비어있을 수 없습니다.")
        String releaseNote
) {
}
