package com.coffee_is_essential.iot_cloud_ota.service;

import com.coffee_is_essential.iot_cloud_ota.domain.DeployTargetDeviceInfo;
import com.coffee_is_essential.iot_cloud_ota.dto.FirmwareDeploymentRequestDto;
import com.coffee_is_essential.iot_cloud_ota.entity.FirmwareMetadata;
import com.coffee_is_essential.iot_cloud_ota.repository.DeviceJpaRepository;
import com.coffee_is_essential.iot_cloud_ota.repository.FirmwareMetadataJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@Service
@RequiredArgsConstructor
public class FirmwareDeploymentService {
    private final FirmwareMetadataJpaRepository firmwareMetadataJpaRepository;
    private final DeviceJpaRepository deviceJpaRepository;

    public void deployFirmware(Long firmwareId, FirmwareDeploymentRequestDto requestDto) {
        FirmwareMetadata findFirmware = firmwareMetadataJpaRepository.findByIdOrElseThrow(firmwareId);

        if (requestDto.deviceIds().isEmpty() && requestDto.groupIds().isEmpty() && requestDto.regionIds().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");
        }

        List<DeployTargetDeviceInfo> devices = deviceJpaRepository.findByFilterDynamic(
                requestDto.deviceIds(),
                requestDto.groupIds(),
                requestDto.regionIds()
        );

    }
}
