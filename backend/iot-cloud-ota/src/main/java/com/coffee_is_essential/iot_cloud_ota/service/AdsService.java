package com.coffee_is_essential.iot_cloud_ota.service;

import com.coffee_is_essential.iot_cloud_ota.domain.PaginationInfo;
import com.coffee_is_essential.iot_cloud_ota.domain.S3FileHashResult;
import com.coffee_is_essential.iot_cloud_ota.dto.*;
import com.coffee_is_essential.iot_cloud_ota.entity.ActiveDeviceInfo;
import com.coffee_is_essential.iot_cloud_ota.entity.AdsMetadata;
import com.coffee_is_essential.iot_cloud_ota.repository.AdsMetadataJpaRepository;
import com.coffee_is_essential.iot_cloud_ota.repository.DeviceAdsJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdsService {
    private final AdsMetadataJpaRepository adsMetadataJpaRepository;
    private final DeviceAdsJpaRepository deviceAdsJpaRepository;
    private final S3Service s3Service;
    private final CloudFrontSignedUrlService cloudFrontSignedUrlService;
    private static final int TIMEOUT = 10;

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
    public SaveAdvertisementMetadataResponseDto saveAdvertisementMetadata(AdsMetadataRequestDto requestDto) {
        if (adsMetadataJpaRepository.findByTitle(requestDto.title()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, requestDto.title() + "의 광고 제목이 이미 존재합니다.");
        }

        if (adsMetadataJpaRepository.existsByOriginalS3Path(requestDto.originalS3Path())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "S3 경로 '" + requestDto.originalS3Path() + "'에 이미 광고가 존재합니다.");
        }

        S3FileHashResult result = s3Service.calculateS3FileHash(requestDto.binaryS3Path());
        AdsMetadata adsMetadata = new AdsMetadata(
                requestDto.title(),
                requestDto.description(),
                requestDto.originalS3Path(),
                requestDto.binaryS3Path(),
                result.fileHash(),
                result.fileSize()
        );

        AdsMetadata savedAdsMetadata = adsMetadataJpaRepository.save(adsMetadata);

        return SaveAdvertisementMetadataResponseDto.from(savedAdsMetadata);
    }

    /**
     * 광고 메타데이터를 페이지네이션 방식으로 조회합니다.
     * 검색어(keyword)가 주어지면 제목/설명에 대해 부분 검색 수행
     * 최신순(createdAt 기준 내림차순)으로 정렬
     * 조회된 엔티티를 응답 DTO로 변환하면서 Presigned 다운로드 URL 생성
     * 페이지네이션 메타데이터(Page 번호, 크기, 전체 페이지 수, 전체 아이템 수) 생성
     *
     * @param paginationInfo 페이지 번호, 페이지 크기, 검색어 정보를 담은 DTO
     * @return 광고 메타데이터 리스트와 페이지네이션 정보를 포함한 응답 DTO
     */
    public AdsMetadataWithPageResponseDto findAllWithPagination(PaginationInfo paginationInfo) {
        Pageable pageable = PageRequest.of(paginationInfo.page() - 1, paginationInfo.limit(), Sort.by("createdAt").descending());
        String keyword = paginationInfo.search();

        Page<AdsMetadata> findAds = adsMetadataJpaRepository.searchWithNullableKeyword(
                keyword, pageable
        );

        List<AdsMetadataResponseDto> ads = findAds.getContent().stream()
                .map(ad -> AdsMetadataResponseDto.from(ad, cloudFrontSignedUrlService.generateAdsSignedUrl(ad.getTitle()).url()))
                .toList();

        PaginationMetadataDto metadataDto = new PaginationMetadataDto(
                findAds.getPageable().getPageNumber() + 1,
                findAds.getPageable().getPageSize(),
                findAds.getTotalPages(),
                findAds.getTotalElements()
        );

        return new AdsMetadataWithPageResponseDto(ads, metadataDto);
    }

    /**
     * ID로 광고 메타데이터를 조회합니다.
     * 광고 메타데이터가 존재하지 않으면 404 예외 발생
     * 광고 메타데이터에 대한 Presigned 다운로드 URL 생성
     * 해당 광고가 활성화된(endedAt이 NULL인) 디바이스 목록 조회
     *
     * @param id 광고 메타데이터 ID
     * @return 광고 메타데이터 상세 정보와 활성화된 디바이스 목록을 담은 응답 DTO
     */
    public AdsDetailResponseDto findById(Long id) {
        AdsMetadata adsMetadata = adsMetadataJpaRepository.findByIdOrElseThrow(id);
        Date expiresAt = Date.from(Instant.now().plus(Duration.ofMinutes(TIMEOUT)));

        AdsMetadataResponseDto adsMetadataResponseDto = AdsMetadataResponseDto.from(
                adsMetadata,
                cloudFrontSignedUrlService.generateSignedUrl(adsMetadata.getOriginalS3Path(), expiresAt)
        );

        List<ActiveDeviceInfo> activeDevicesSummaryByAds = deviceAdsJpaRepository.findActiveDevicesByAdsId(adsMetadata.getId());

        return new AdsDetailResponseDto(adsMetadataResponseDto, activeDevicesSummaryByAds);
    }
}
