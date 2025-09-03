package com.coffee_is_essential.iot_cloud_ota.service;

import com.coffee_is_essential.iot_cloud_ota.domain.PaginationInfo;
import com.coffee_is_essential.iot_cloud_ota.domain.S3FileHashResult;
import com.coffee_is_essential.iot_cloud_ota.dto.*;
import com.coffee_is_essential.iot_cloud_ota.entity.AdvertisementMetadata;
import com.coffee_is_essential.iot_cloud_ota.repository.AdvertisementMetadataJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

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
    public SaveAdvertisementMetadataResponseDto saveAdvertisementMetadata(AdvertisementMetadataRequestDto requestDto) {
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

        return SaveAdvertisementMetadataResponseDto.from(savedAdvertisementMetadata);
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
    public AdvertisementMetadataWithPageResponseDto findAllWithPagination(PaginationInfo paginationInfo) {
        Pageable pageable = PageRequest.of(paginationInfo.page() - 1, paginationInfo.limit(), Sort.by("createdAt").descending());
        String keyword = paginationInfo.search();

        Page<AdvertisementMetadata> findAds = advertisementMetadataJpaRepository.searchWithNullableKeyword(
                keyword, pageable
        );

        List<AdvertisementMetadataResponseDto> ads = findAds.getContent().stream()
                .map(ad -> AdvertisementMetadataResponseDto.from(ad, s3Service.getAdsPresignedDownloadUrl(ad.getTitle()).url()))
                .toList();

        PaginationMetadataDto metadataDto = new PaginationMetadataDto(
                findAds.getPageable().getPageNumber() + 1,
                findAds.getPageable().getPageSize(),
                findAds.getTotalPages(),
                findAds.getTotalElements()
        );

        return new AdvertisementMetadataWithPageResponseDto(ads, metadataDto);
    }
}
