package com.coffee_is_essential.iot_cloud_ota.service;

import com.coffee_is_essential.iot_cloud_ota.domain.*;
import com.coffee_is_essential.iot_cloud_ota.dto.DetailDeploymentResponseDto;
import com.coffee_is_essential.iot_cloud_ota.dto.FirmwareDeploymentDto;
import com.coffee_is_essential.iot_cloud_ota.dto.FirmwareDeploymentListDto;
import com.coffee_is_essential.iot_cloud_ota.dto.FirmwareDeploymentRequestDto;
import com.coffee_is_essential.iot_cloud_ota.entity.*;
import com.coffee_is_essential.iot_cloud_ota.enums.DeploymentStatus;
import com.coffee_is_essential.iot_cloud_ota.enums.DeploymentType;
import com.coffee_is_essential.iot_cloud_ota.enums.OverallStatus;
import com.coffee_is_essential.iot_cloud_ota.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FirmwareDeploymentService {
    private final ObjectMapper objectMapper;
    @Value("${mqtt.handler.base.url}")
    private String mqttHandlerBaseUrl;

    private final FirmwareMetadataJpaRepository firmwareMetadataJpaRepository;
    private final FirmwareDeploymentRepository firmwareDeploymentRepository;
    private final FirmwareDeploymentDeviceRepository firmwareDeploymentDeviceRepository;
    private final DeviceJpaRepository deviceJpaRepository;
    private final OverallDeploymentStatusRepository overallDeploymentStatusRepository;
    private final RestClient restClient;
    private final QuestDbRepository questDbRepository;
    private final CloudFrontSignedUrlService cloudFrontSignedUrlService;
    private final DeploymentRedisService deploymentRedisService;
    private static final int TIMEOUT = 10;

    /**
     * 펌웨어를 지정된 기기/그룹/리전에 배포 요청합니다.
     * 1. 펌웨어 메타데이터 조회
     * 2. 배포 대상(Device) 필터링
     * 3. CloudFront Signed URL 생성
     * 4. FirmwareDeployment 엔티티 저장
     * 5. FirmwareDeploymentDevice 엔티티 저장 (대상 장치별 상태 기록)
     * 6. MQTT Handler에 배포 요청 전송
     *
     * @param firmwareId 배포할 펌웨어 메타데이터 ID
     * @param requestDto 배포 요청 DTO (대상 장치/그룹/리전, 배포 타입 등)
     * @return 배포 요청 결과 DTO
     */
    @Transactional
    public FirmwareDeploymentDto deployFirmware(Long firmwareId, FirmwareDeploymentRequestDto requestDto) {
        FirmwareMetadata findFirmware = firmwareMetadataJpaRepository.findByIdOrElseThrow(firmwareId);

        if (requestDto.deviceIds().isEmpty() && requestDto.groupIds().isEmpty() && requestDto.regionIds().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");
        }

        List<Device> devices = deviceJpaRepository.findByFilterDynamic(
                requestDto.deviceIds(),
                requestDto.groupIds(),
                requestDto.regionIds()
        );

        Date expiresAt = Date.from(Instant.now().plus(Duration.ofMinutes(TIMEOUT)));
        FirmwareDeployInfo deployInfo = FirmwareDeployInfo.from(findFirmware, expiresAt);
        String signedUrl = cloudFrontSignedUrlService.generateSignedUrl(findFirmware.getS3Path(), expiresAt);

        FirmwareDeployment firmwareDeployment = new FirmwareDeployment(findFirmware, deployInfo.deploymentId(), requestDto.deploymentType(), deployInfo.deployedAt().toLocalDateTime(), deployInfo.expiresAt().toLocalDateTime());
        firmwareDeploymentRepository.save(firmwareDeployment);
        overallDeploymentStatusRepository.save(new OverallDeploymentStatus(firmwareDeployment, OverallStatus.IN_PROGRESS));
        saveFirmwareDeploymentDevices(devices, firmwareDeployment, DeploymentStatus.IN_PROGRESS);

        List<DeployTargetDeviceInfo> deviceInfos = devices
                .stream()
                .map(DeployTargetDeviceInfo::from)
                .toList();

        if (deviceInfos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");
        }

        FirmwareDeploymentDto deploymentDto = new FirmwareDeploymentDto(signedUrl, deployInfo, deviceInfos, TIMEOUT);
        sendMqttHandler(deploymentDto);
        deploymentRedisService.addDevices(firmwareDeployment.getCommandId(), deviceInfos);
        return deploymentDto;
    }

    /**
     * 대상 장치 목록을 FirmwareDeploymentDevice 엔티티로 변환 후 DB에 저장
     *
     * @param devices            배포 대상 장치 목록
     * @param firmwareDeployment 배포 엔티티
     * @param deploymentStatus   초기 배포 상태 (보통 IN_PROGRESS)
     */
    private void saveFirmwareDeploymentDevices(List<Device> devices, FirmwareDeployment firmwareDeployment, DeploymentStatus deploymentStatus) {
        for (Device device : devices) {
            FirmwareDeploymentDevice firmwareDeploymentDevice = new FirmwareDeploymentDevice(device, firmwareDeployment, deploymentStatus);
            firmwareDeploymentDeviceRepository.save(firmwareDeploymentDevice);
        }
    }

    /**
     * MQTT Handler API 호출
     * 배포 요청 DTO를 JSON 직렬화 후 POST 요청 전송
     *
     * @param firmwareDeploymentDto 배포 요청 DTO
     */
    private void sendMqttHandler(FirmwareDeploymentDto firmwareDeploymentDto) {
        String url = mqttHandlerBaseUrl + "/api/firmwares/deployment";

        try {
            String templateJson = objectMapper.writeValueAsString(firmwareDeploymentDto);

            String response = restClient.post()
                    .uri(url)
                    .body(templateJson)
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "mqtt handler 호출 실패", e);
        }
    }

    /**
     * 펌웨어 배포 이력 목록 조회 (페이지네이션 적용)
     *
     * @param paginationInfo 페이지 번호/사이즈 정보
     * @return 배포 목록 + 페이지네이션 메타데이터
     */
    public FirmwareDeploymentListDto getFirmwareDeploymentList(PaginationInfo paginationInfo) {
        Pageable pageable = PageRequest.of(paginationInfo.page() - 1, paginationInfo.limit(), Sort.by("createdAt").descending());
        Page<FirmwareDeployment> deploymentPage = firmwareDeploymentRepository.findAll(pageable);
        List<FirmwareDeployment> deployments = deploymentPage.getContent();
        List<FirmwareDeploymentMetadata> list = new ArrayList<>();

        for (FirmwareDeployment firmwareDeployment : deployments) {
            FirmwareDeploymentMetadata metadata = getFirmwareDeploymentMetadata(firmwareDeployment);
            list.add(metadata);
        }

        return FirmwareDeploymentListDto.of(list, deploymentPage.getPageable(), deploymentPage.getTotalPages(), deploymentPage.getTotalElements());
    }

    /**
     * 단일 배포 엔티티를 Metadata DTO로 변환
     * 상태별 카운트 조회
     * 대상(Target) 정보 조회 (Device, Division, Region 별 분기)
     *
     * @param firmwareDeployment 배포 엔티티
     * @return FirmwareDeploymentMetadata DTO
     */
    private FirmwareDeploymentMetadata getFirmwareDeploymentMetadata(FirmwareDeployment firmwareDeployment) {
        Long deploymentId = firmwareDeployment.getId();
        List<DeploymentStatusCount> countList = firmwareDeploymentDeviceRepository.countStatusByLatestDeployment(deploymentId);
        List<Target> targetInfo = getTargetList(firmwareDeployment);
        OverallDeploymentStatus status = overallDeploymentStatusRepository.findLatestByDeploymentIdOrElseThrow(firmwareDeployment.getId());

        return FirmwareDeploymentMetadata.of(firmwareDeployment, targetInfo, ProgressCount.from(countList), status.getOverallStatus());
    }

    /**
     * 배포 ID로 상세 정보를 조회합니다.
     * 배포 메타데이터, 전체 상태, 대상 목록, 상태 집계,
     * 디바이스별 최신 다운로드 이벤트를 조합해 DTO를 반환합니다.
     *
     * @param id 배포 ID
     * @return 배포 상세 DTO
     */
    public DetailDeploymentResponseDto findFirmwareDeploymentById(Long id) {
        FirmwareDeployment deployment = firmwareDeploymentRepository.findByIdOrElseThrow(id);
        OverallDeploymentStatus status = overallDeploymentStatusRepository.findLatestByDeploymentIdOrElseThrow(id);
        List<Target> targetInfo = getTargetList(deployment);
        List<DeploymentStatusCount> countList = firmwareDeploymentDeviceRepository.countStatusByLatestDeployment(id);
        List<DeviceDeploymentStatus> downloadEvents = questDbRepository.findLatestPerDeviceByCommandId(deployment.getCommandId()).stream()
                .map(DeviceDeploymentStatus::from)
                .toList();

        return DetailDeploymentResponseDto.of(deployment, targetInfo, downloadEvents, countList, status);
    }

    /**
     * 배포 타입에 따라 대상(Device/Division/Region) 목록을 조회합니다.
     *
     * @param firmwareDeployment 배포 엔티티
     * @return 대상 정보 리스트
     */
    private List<Target> getTargetList(FirmwareDeployment firmwareDeployment) {
        List<Target> targetInfo = new ArrayList<>();

        if (firmwareDeployment.getDeploymentType().equals(DeploymentType.DEVICE)) {
            targetInfo = firmwareDeploymentDeviceRepository.findDeviceInfoByDeploymentId(firmwareDeployment.getId());
        } else if (firmwareDeployment.getDeploymentType().equals(DeploymentType.DIVISION)) {
            targetInfo = firmwareDeploymentDeviceRepository.findDivisionInfoByDeploymentId(firmwareDeployment.getId());
        } else if (firmwareDeployment.getDeploymentType().equals(DeploymentType.REGION)) {
            targetInfo = firmwareDeploymentDeviceRepository.findRegionInfoByDeploymentId(firmwareDeployment.getId());
        }

        return targetInfo;
    }
}
