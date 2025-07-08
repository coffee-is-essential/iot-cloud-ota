package com.coffee_is_essential.iot_cloud_ota.dto;

import com.coffee_is_essential.iot_cloud_ota.entity.FirmwareMetadata;

import java.time.LocalDateTime;

/**
 * 펌웨어 메타데이터 응답을 위한 DTO 입니다.
 *
 * @param id          펌웨어 메타데이터의 고유 ID
 * @param version     펌웨어 버전
 * @param fileName    펌웨어 파일 이름
 * @param releaseNote 릴리즈 노트
 * @param createdAt   생성 시각
 * @param modifiedAt  마지막 수정 시각
 */
public record FirmwareMetadataResponseDto(
        Long id,
        String version,
        String fileName,
        String releaseNote,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
) {

    /**
     * 엔티티로부터 response DTO 객체를 생성합니다.
     *
     * @param firmwareMetadata 변환할 펌웨어 메타데이터 엔티티
     * @return 변환된 response DTO
     */
    public static FirmwareMetadataResponseDto from(FirmwareMetadata firmwareMetadata) {
        return new FirmwareMetadataResponseDto(
                firmwareMetadata.getId(),
                firmwareMetadata.getVersion(),
                firmwareMetadata.getFileName(),
                firmwareMetadata.getReleaseNote(),
                firmwareMetadata.getCreatedAt(),
                firmwareMetadata.getModifiedAt()
        );
    }
}
