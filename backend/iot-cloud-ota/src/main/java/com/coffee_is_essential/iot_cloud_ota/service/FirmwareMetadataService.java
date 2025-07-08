package com.coffee_is_essential.iot_cloud_ota.service;

import com.coffee_is_essential.iot_cloud_ota.dto.FirmwareMetadataRequestDto;
import com.coffee_is_essential.iot_cloud_ota.dto.FirmwareMetadataResponseDto;
import com.coffee_is_essential.iot_cloud_ota.entity.FirmwareMetadata;
import com.coffee_is_essential.iot_cloud_ota.repository.FirmwareMetadataJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FirmwareMetadataService {
    private final FirmwareMetadataJpaRepository firmwareMetadataJpaRepository;

    public FirmwareMetadataResponseDto saveFirmwareMetadata(FirmwareMetadataRequestDto requestDto) {
        FirmwareMetadata firmwareMetadata = new FirmwareMetadata(
                requestDto.version(),
                requestDto.fileName(),
                requestDto.releaseNote()
        );

        FirmwareMetadata savedFirmwareMetadata = firmwareMetadataJpaRepository.save(firmwareMetadata);

        return FirmwareMetadataResponseDto.from(savedFirmwareMetadata);
    }
}
