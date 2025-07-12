package com.coffee_is_essential.iot_cloud_ota.service;

import com.coffee_is_essential.iot_cloud_ota.dto.FirmwareMetadataRequestDto;
import com.coffee_is_essential.iot_cloud_ota.dto.FirmwareMetadataResponseDto;
import com.coffee_is_essential.iot_cloud_ota.dto.FirmwareMetadataWithPageResponseDto;
import com.coffee_is_essential.iot_cloud_ota.dto.PaginationMetadataDto;
import com.coffee_is_essential.iot_cloud_ota.entity.FirmwareMetadata;
import com.coffee_is_essential.iot_cloud_ota.repository.FirmwareMetadataJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * 펌웨어 메타데이터의 비즈니스 로직을 처리하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class FirmwareMetadataService {
    private final FirmwareMetadataJpaRepository firmwareMetadataJpaRepository;

    /**
     * 펌웨어 메타데이터를 저장하고, 저장된 결과를 응답 DTO로 반환합니다.
     *
     * @param requestDto 저장할 펌웨어 메타데이터 요청 DTO
     * @return 저장된 펌웨어 정보를 담은 응답 DTO
     */
    @Transactional
    public FirmwareMetadataResponseDto saveFirmwareMetadata(FirmwareMetadataRequestDto requestDto) {
        FirmwareMetadata firmwareMetadata = new FirmwareMetadata(
                requestDto.version(),
                requestDto.fileName(),
                requestDto.releaseNote(),
                requestDto.s3Path()
        );

        FirmwareMetadata savedFirmwareMetadata = firmwareMetadataJpaRepository.save(firmwareMetadata);

        return FirmwareMetadataResponseDto.from(savedFirmwareMetadata);
    }

    /**
     * 페이지 번호와 페이지 크기를 기반으로 펌웨어 메타데이터 목록을 페이지네이션하여 조회합니다.
     * 검색어가 제공된 경우, 펌웨어 버전(version) 또는 릴리즈 노트(release_note)에 해당 문자열이 포함된 항목만 조회합니다.
     * 그렇지 않은 경우 전체 데이터를 페이지네이션하여 반환합니다.
     *
     * @param page   조회할 페이지 번호 (1부터 시작)
     * @param limit  페이지당 항목 수
     * @param search (선택) 검색어 - version 또는 release_note 컬럼에 포함될 문자열
     * @return 조회된 펌웨어 메타데이터 목록과 페이지 메타데이터가 포함된 DTO
     */
    public FirmwareMetadataWithPageResponseDto findAllWithPagination(int page, int limit, String search) {
        if (page < 0 || limit < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "올바르지 않은 요청입니다.");
        }

        int offset = (page - 1) * limit;
        List<FirmwareMetadata> list;
        long totalCount;

        if (search == null || search.isBlank()) {
            list = firmwareMetadataJpaRepository.findFirmwareMetadataPage(limit, offset);
            totalCount = firmwareMetadataJpaRepository.countAllFirmwareMetadata();
        } else {
            String keyWord = "%" + search + "%";
            list = firmwareMetadataJpaRepository.searchFirmwareMetadataByVersionOrReleaseNote(limit, offset, keyWord);
            totalCount = firmwareMetadataJpaRepository.countFirmwareMetadataByVersionOrReleaseNote(keyWord);
        }

        List<FirmwareMetadataResponseDto> content = list.stream()
                .map(FirmwareMetadataResponseDto::from)
                .toList();

        PaginationMetadataDto paginationMetadataDto = new PaginationMetadataDto(
                page,
                limit,
                (int) Math.ceil((double) totalCount / limit),
                totalCount
        );

        return new FirmwareMetadataWithPageResponseDto(content, paginationMetadataDto);
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
