package com.coffee_is_essential.iot_cloud_ota.controller;

import com.coffee_is_essential.iot_cloud_ota.domain.PaginationInfo;
import com.coffee_is_essential.iot_cloud_ota.dto.*;
import com.coffee_is_essential.iot_cloud_ota.service.DeploymentService;
import com.coffee_is_essential.iot_cloud_ota.service.FirmwareMetadataService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 펌웨어 관련 요청을 처리하는 REST 컨트롤러입니다.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/firmwares")
public class FirmwareController {
    private final FirmwareMetadataService firmwareMetadataService;
    private final DeploymentService deploymentService;

    /**
     * 펌웨어 메타데이터를 저장합니다.
     *
     * @param requestDto 저장할 펌웨어 메타데이터를 포함한 요청 DTO
     * @return 저장된 펌웨어 메타데이터 정보가 포함된 응답 DTO
     */
    @PostMapping("/metadata")
    public ResponseEntity<FirmwareMetadataResponseDto> saveFirmwareMetadata(@Valid @RequestBody FirmwareMetadataRequestDto requestDto) {
        FirmwareMetadataResponseDto responseDto = firmwareMetadataService.saveFirmwareMetadata(requestDto);

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    /**
     * 펌웨어 메타데이터 목록을 페이지네이션하여 조회합니다.
     *
     * @param page   조회할 페이지 번호 (기본값: 1)
     * @param limit  페이지당 항목 수 (기본값: 10)
     * @param search 검색어 (선택 사항) - 펌웨어 버전 또는 릴리즈 노트 내용을 기준으로 검색
     * @return 페이징된 펌웨어 메타데이터 목록과 페이지 정보가 포함된 응답 DTO
     */
    @GetMapping("/metadata")
    public ResponseEntity<FirmwareMetadataWithPageResponseDto> findAllWithPagination(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String search
    ) {
        PaginationInfo paginationInfo = new PaginationInfo(page, limit, search);
        FirmwareMetadataWithPageResponseDto responseDto = firmwareMetadataService.findAllWithPagination(paginationInfo);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    /**
     * 지정한 펌웨어 ID에 해당하는 펌웨어 메타데이터를 조회합니다.
     *
     * @param id 조회할 펌웨어 메타데이터의 고유 ID
     * @return 조회된 펌웨어 메타데이터 정보가 포함된 응답 DTO
     */
    @GetMapping("/metadata/{id}")
    public ResponseEntity<FirmwareMetadataResponseDto> findById(@PathVariable Long id) {
        FirmwareMetadataResponseDto responseDto = firmwareMetadataService.findById(id);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    /**
     * 지정한 펌웨어 메타데이터 ID를 기반으로 펌웨어 배포를 수행합니다.
     *
     * @param id         배포할 펌웨어 메타데이터 ID
     * @param requestDto 배포 요청에 필요한 대상 기기 정보 및 설정 값
     * @return 배포 결과 정보가 포함된 DTO
     */
    @PostMapping("/metadata/{id}/deployment")
    public ResponseEntity<FirmwareDeploymentDto> deploy(@PathVariable Long id, @RequestBody FirmwareDeploymentRequestDto requestDto) {
        FirmwareDeploymentDto dto = deploymentService.deployFirmware(id, requestDto);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    /**
     * 펌웨어 배포 이력을 페이지네이션하여 조회합니다.
     *
     * @param page  조회할 페이지 번호 (기본값: 1)
     * @param limit 페이지당 항목 수 (기본값: 10)
     * @return 페이징된 펌웨어 배포 목록 DTO
     */
    @GetMapping("/deployment/list")
    public ResponseEntity<FirmwareDeploymentListDto> findAllDeploymentList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        PaginationInfo paginationInfo = PaginationInfo.of(page, limit);
        FirmwareDeploymentListDto list = deploymentService.getFirmwareDeploymentList(paginationInfo);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    /**
     * 특정 ID에 해당하는 펌웨어 배포 상세 정보를 조회합니다.
     * 배포 식별자 ID를 Path Variable로 받아서,
     * 해당 배포의 세부 내역(대상 장치, 상태, 메타데이터 등)을 반환합니다.
     *
     * @param id 조회할 펌웨어 배포의 고유 식별자
     * @return 배포 상세 정보를 담은 DTO
     */
    @GetMapping("/deployment/{id}")
    public ResponseEntity<DetailDeploymentResponseDto> findDetailDeploymentById(@PathVariable Long id) {
        DetailDeploymentResponseDto dto = deploymentService.findFirmwareDeploymentById(id);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
}
