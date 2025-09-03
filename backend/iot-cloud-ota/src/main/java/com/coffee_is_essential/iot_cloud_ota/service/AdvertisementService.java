package com.coffee_is_essential.iot_cloud_ota.service;

import com.coffee_is_essential.iot_cloud_ota.domain.S3FileHashResult;
import com.coffee_is_essential.iot_cloud_ota.dto.AdvertisementMetadataRequestDto;
import com.coffee_is_essential.iot_cloud_ota.dto.AdvertisementMetadataResponseDto;
import com.coffee_is_essential.iot_cloud_ota.entity.AdvertisementMetadata;
import com.coffee_is_essential.iot_cloud_ota.repository.AdvertisementMetadataJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AdvertisementService {
    private final AdvertisementMetadataJpaRepository advertisementMetadataJpaRepository;
    private final S3Service s3Service;

    /**
     * 광고 메타데이터를 저장합니다.
     * 다음과 같은 검증을 수행합니다:
     * 동일한 광고 제목이 이미 존재하는지 확인
     * 동일한 원본 S3 경로가 이미 존재하는지 확인
     * 검증 후에는 S3에 저장된 바이너리 파일의 해시 및 파일 크기를 계산하여
     * 광고 메타데이터 엔티티를 생성하고 저장합니다.
     *
     * @param requestDto 광고 제목, 설명, 원본/바이너리 S3 경로 정보를 담은 요청 DTO
     * @return 저장된 광고 메타데이터 응답 DTO
     */
    @Transactional
    public AdvertisementMetadataResponseDto saveAdvertisementMetadata(AdvertisementMetadataRequestDto requestDto) {
        if (advertisementMetadataJpaRepository.findByTitle(requestDto.title()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, requestDto.title() + "의 광고 제목이 이미 존재합니다.");
        }

        if (advertisementMetadataJpaRepository.existsByOriginalS3Path(requestDto.originalS3Path())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "S3 경로 '" + requestDto.originalS3Path() + "'에 이미 광고가 존재합니다.");
        }

        S3FileHashResult result = s3Service.calculateS3FileHash(requestDto.binaryS3Path());
        AdvertisementMetadata advertisementMetadata = new AdvertisementMetadata(
                requestDto.title(),
                requestDto.description(),
                requestDto.originalS3Path(),
                requestDto.binaryS3Path(),
                result.fileHash(),
                result.fileSize()
        );

        AdvertisementMetadata savedAdvertisementMetadata = advertisementMetadataJpaRepository.save(advertisementMetadata);

        return AdvertisementMetadataResponseDto.from(savedAdvertisementMetadata);
    }
}
