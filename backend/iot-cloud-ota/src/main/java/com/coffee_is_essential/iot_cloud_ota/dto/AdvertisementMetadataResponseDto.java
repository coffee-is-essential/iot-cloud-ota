package com.coffee_is_essential.iot_cloud_ota.dto;

import com.coffee_is_essential.iot_cloud_ota.entity.AdvertisementMetadata;

import java.time.OffsetDateTime;

/**
 * 광고 메타데이터 저장 응답 DTO입니다.
 *
 * @param id          광고 메타데이터 ID
 * @param title       광고 제목
 * @param description 광고 설명
 * @param createdAt   생성 시각
 * @param modifiedAt  수정 시각
 */
public record AdvertisementMetadataResponseDto(
        Long id,
        String title,
        String description,
        OffsetDateTime createdAt,
        OffsetDateTime modifiedAt
) {
    public static AdvertisementMetadataResponseDto from(AdvertisementMetadata advertisementMetadata) {
        return new AdvertisementMetadataResponseDto(
                advertisementMetadata.getId(),
                advertisementMetadata.getTitle(),
                advertisementMetadata.getDescription(),
                advertisementMetadata.getCreatedAt(),
                advertisementMetadata.getModifiedAt()
        );
    }
}
