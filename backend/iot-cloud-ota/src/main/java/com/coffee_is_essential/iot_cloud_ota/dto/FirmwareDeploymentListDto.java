package com.coffee_is_essential.iot_cloud_ota.dto;

import com.coffee_is_essential.iot_cloud_ota.domain.FirmwareDeploymentMetadata;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 펌웨어 배포 이력 목록 + 페이지네이션 메타데이터를 담는 응답 DTO.
 * Controller의 /deployment/list API 응답으로 사용됩니다.
 * items: 배포 메타데이터 리스트
 * paginationMeta: 현재 페이지, 페이지 크기, 전체 페이지 수, 전체 요소 수
 *
 * @param items          배포 메타데이터 리스트 (FirmwareDeploymentMetadata DTO 목록)
 * @param paginationMeta 페이지네이션 관련 정보 (현재 페이지, 크기, 전체 페이지 수, 전체 개수)
 */
public record FirmwareDeploymentListDto(
        List<FirmwareDeploymentMetadata> items,
        PaginationMetadataDto paginationMeta
) {
    public static FirmwareDeploymentListDto of(List<FirmwareDeploymentMetadata> items, Pageable pageable, int totalPages, long totalElements) {
        PaginationMetadataDto metadataDto = new PaginationMetadataDto(
                pageable.getPageNumber() + 1,
                pageable.getPageSize(),
                totalPages,
                totalElements
        );

        return new FirmwareDeploymentListDto(items, metadataDto);
    }
}
