package com.coffee_is_essential.iot_cloud_ota.service;

import com.coffee_is_essential.iot_cloud_ota.dto.FirmwareMetadataRequestDto;
import com.coffee_is_essential.iot_cloud_ota.dto.FirmwareMetadataResponseDto;
import com.coffee_is_essential.iot_cloud_ota.entity.FirmwareMetadata;
import com.coffee_is_essential.iot_cloud_ota.repository.FirmwareMetadataJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
