package com.coffee_is_essential.iot_cloud_ota.service;

import com.coffee_is_essential.iot_cloud_ota.domain.*;
import com.coffee_is_essential.iot_cloud_ota.dto.FirmwareDeploymentDto;
import com.coffee_is_essential.iot_cloud_ota.dto.FirmwareDeploymentListDto;
import com.coffee_is_essential.iot_cloud_ota.dto.FirmwareDeploymentRequestDto;
import com.coffee_is_essential.iot_cloud_ota.entity.Device;
import com.coffee_is_essential.iot_cloud_ota.entity.FirmwareDeployment;
import com.coffee_is_essential.iot_cloud_ota.entity.FirmwareDeploymentDevice;
import com.coffee_is_essential.iot_cloud_ota.entity.FirmwareMetadata;
import com.coffee_is_essential.iot_cloud_ota.enums.DeploymentStatus;
import com.coffee_is_essential.iot_cloud_ota.enums.OverallDeploymentStatus;
import com.coffee_is_essential.iot_cloud_ota.repository.DeviceJpaRepository;
import com.coffee_is_essential.iot_cloud_ota.repository.FirmwareDeploymentDeviceRepository;
import com.coffee_is_essential.iot_cloud_ota.repository.FirmwareDeploymentRepository;
import com.coffee_is_essential.iot_cloud_ota.repository.FirmwareMetadataJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
    private final RestClient restClient;
    private final CloudFrontSignedUrlService cloudFrontSignedUrlService;
    private static final int TIMEOUT = 10;

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
        String signedUrl;

        try {
            signedUrl = cloudFrontSignedUrlService.generateSignedUrl(findFirmware.getS3Path(), expiresAt);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "CloudFront 서명 URL 생성 실패", e);
        }

        FirmwareDeployment firmwareDeployment = new FirmwareDeployment(findFirmware, deployInfo.deploymentId(), requestDto.deploymentType(), deployInfo.deployedAt().toLocalDateTime(), deployInfo.expiresAt().toLocalDateTime(), OverallDeploymentStatus.IN_PROGRESS);
        firmwareDeploymentRepository.save(firmwareDeployment);
        saveFirmwareDeploymentDevices(devices, firmwareDeployment, DeploymentStatus.IN_PROGRESS);

        List<DeployTargetDeviceInfo> deviceInfos = devices
                .stream()
                .map(DeployTargetDeviceInfo::from)
                .toList();

        FirmwareDeploymentDto deploymentDto = new FirmwareDeploymentDto(signedUrl, deployInfo, deviceInfos);
//        sendMqttHandler(deploymentDto);
        return deploymentDto;
    }

    private void saveFirmwareDeploymentDevices(List<Device> devices, FirmwareDeployment firmwareDeployment, DeploymentStatus deploymentStatus) {
        for (Device device : devices) {
            FirmwareDeploymentDevice firmwareDeploymentDevice = new FirmwareDeploymentDevice(device, firmwareDeployment, deploymentStatus);
            firmwareDeploymentDeviceRepository.save(firmwareDeploymentDevice);
        }
    }

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

    public FirmwareDeploymentListDto getFirmwareDeploymentList() {
        List<FirmwareDeployment> deployments = firmwareDeploymentRepository.findAll();
        List<FirmwareDeploymentMetadata> list = new ArrayList<>();

        for (FirmwareDeployment firmwareDeployment : deployments) {
            FirmwareDeploymentMetadata metadata = getFirmwareDeploymentMetadata(firmwareDeployment);
            list.add(metadata);
        }

        return null;
    }

    private FirmwareDeploymentMetadata getFirmwareDeploymentMetadata(FirmwareDeployment firmwareDeployment) {
        Long deploymentId = firmwareDeployment.getId();
        Firmware firmware = Firmware.from(firmwareDeployment.getFirmwareMetadata());
        List<FirmwareDeploymentDevice> firmwareDeploymentDeviceList = firmwareDeploymentDeviceRepository.findLatestByDeploymentId(deploymentId);

        System.out.println("firmwareDeploymentDeviceList = " + firmwareDeploymentDeviceList);

        return null;
    }
}
