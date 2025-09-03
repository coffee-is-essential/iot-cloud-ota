package com.coffee_is_essential.iot_cloud_ota.dto;

import com.coffee_is_essential.iot_cloud_ota.entity.AdvertisementMetadata;

import java.time.OffsetDateTime;

/**
 * 광고 메타데이터 응답 DTO입니다.
 * 광고 기본 정보와 함께 원본 파일에 접근할 수 있는 서명된 URL을 제공합니다.
 *
 * @param id               광고 메타데이터 ID
 * @param title            광고 제목
 * @param description      광고 설명
 * @param originalSignedUrl 광고 원본 파일 접근용 Presigned URL
 * @param createdAt        생성 시각
 * @param modifiedAt       수정 시각
 */
public record AdvertisementMetadataResponseDto(
        Long id,
        String title,
        String description,
        String originalSignedUrl,
        OffsetDateTime createdAt,
        OffsetDateTime modifiedAt
) {
    public static AdvertisementMetadataResponseDto from(AdvertisementMetadata advertisementMetadata, String originalSignedUrl) {
        return new AdvertisementMetadataResponseDto(
                advertisementMetadata.getId(),
                advertisementMetadata.getTitle(),
                advertisementMetadata.getDescription(),
                originalSignedUrl,
                advertisementMetadata.getCreatedAt(),
                advertisementMetadata.getModifiedAt()
        );
    }
}
