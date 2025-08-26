package com.coffee_is_essential.iot_cloud_ota.service;

import com.coffee_is_essential.iot_cloud_ota.domain.PaginationInfo;
import com.coffee_is_essential.iot_cloud_ota.domain.S3FileHashResult;
import com.coffee_is_essential.iot_cloud_ota.dto.FirmwareMetadataRequestDto;
import com.coffee_is_essential.iot_cloud_ota.dto.FirmwareMetadataResponseDto;
import com.coffee_is_essential.iot_cloud_ota.dto.FirmwareMetadataWithPageResponseDto;
import com.coffee_is_essential.iot_cloud_ota.entity.FirmwareMetadata;
import com.coffee_is_essential.iot_cloud_ota.repository.FirmwareMetadataJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * 펌웨어 메타데이터의 비즈니스 로직을 처리하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class FirmwareMetadataService {
    private final FirmwareMetadataJpaRepository firmwareMetadataJpaRepository;
    private final S3Service s3Service;

    /**
     * 펌웨어 메타데이터를 저장하고, 저장된 결과를 응답 DTO로 반환합니다.
     * 동일한 버전과 파일 이름의 펌웨어가 이미 존재할 경우 예외를 발생시킵니다.
     * 중복된 S3 경로 등록 시 예외를 발생시킵니다.
     *
     * @param requestDto 저장할 펌웨어 메타데이터 요청 DTO
     * @return 저장된 펌웨어 정보를 담은 응답 DTO
     */
    @Transactional
    public FirmwareMetadataResponseDto saveFirmwareMetadata(FirmwareMetadataRequestDto requestDto) {

        if (firmwareMetadataJpaRepository.findByVersionAndFileName(requestDto.version(), requestDto.fileName()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "버전 '" + requestDto.version() + "' 및 파일명 '" + requestDto.fileName() + "'의 펌웨어가 이미 존재합니다.");
        }

        if (firmwareMetadataJpaRepository.existsByS3Path(requestDto.s3Path())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "S3 경로 '" + requestDto.s3Path() + "'에 이미 펌웨어가 존재합니다.");
        }

        S3FileHashResult result = s3Service.calculateS3FileHash(requestDto.s3Path());
        FirmwareMetadata firmwareMetadata = new FirmwareMetadata(
                requestDto.version(),
                requestDto.fileName(),
                requestDto.releaseNote(),
                requestDto.s3Path(),
                result.fileHash(),
                result.fileSize()
        );

        FirmwareMetadata savedFirmwareMetadata = firmwareMetadataJpaRepository.save(firmwareMetadata);

        return FirmwareMetadataResponseDto.from(savedFirmwareMetadata);
    }

    /**
     * 페이지네이션 및 키워드 검색을 통한 펌웨어 메타데이터 목록 조회 메서드입니다.
     * 검색어(keyword)가 null 또는 빈 문자열이면 전체 데이터를 조회합니다.
     * 검색어가 존재하면 version, fileName, releaseNote 컬럼에서 부분 일치 검색을 수행합니다.
     * createdAt 기준 내림차순으로 정렬되며, Pageable을 통해 페이징 처리됩니다.
     *
     * @param paginationInfo 페이지 번호, 페이지 크기, 검색어를 포함한 페이징 정보 DTO
     * @return FirmwareMetadata 목록과 페이징 정보를 포함한 응답 DTO
     */
    public FirmwareMetadataWithPageResponseDto findAllWithPagination(PaginationInfo paginationInfo) {
        Pageable pageable = PageRequest.of(paginationInfo.page() - 1, paginationInfo.limit(), Sort.by("createdAt").descending());
        String keyword = paginationInfo.search();

        Page<FirmwareMetadata> findFirmwareMetadata = firmwareMetadataJpaRepository.searchWithNullableKeyword(
                keyword, pageable
        );

        return FirmwareMetadataWithPageResponseDto.from(findFirmwareMetadata);
    }

    /**
     * 주어진 ID에 해당하는 펌웨어 메타데이터를 조회하여 응답 DTO로 변환합니다.
     *
     * @param id 조회할 펌웨어 메타데이터의 고유 ID
     * @return 조회된 펌웨어 메타데이터의 응답 DTO
     */
    public FirmwareMetadataResponseDto findById(Long id) {
        FirmwareMetadata findFirmwareMetadata = firmwareMetadataJpaRepository.findByIdOrElseThrow(id);

        return FirmwareMetadataResponseDto.from(findFirmwareMetadata);
    }
}
