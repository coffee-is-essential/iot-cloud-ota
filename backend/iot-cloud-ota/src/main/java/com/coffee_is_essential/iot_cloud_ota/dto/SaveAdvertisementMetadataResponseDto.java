package com.coffee_is_essential.iot_cloud_ota.dto;

import com.coffee_is_essential.iot_cloud_ota.entity.AdsMetadata;

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
public record SaveAdvertisementMetadataResponseDto(
        Long id,
        String title,
        String description,
        OffsetDateTime createdAt,
        OffsetDateTime modifiedAt
) {
    public static SaveAdvertisementMetadataResponseDto from(AdsMetadata adsMetadata) {
        return new SaveAdvertisementMetadataResponseDto(
                adsMetadata.getId(),
                adsMetadata.getTitle(),
                adsMetadata.getDescription(),
                adsMetadata.getCreatedAt(),
                adsMetadata.getModifiedAt()
        );
    }
}
