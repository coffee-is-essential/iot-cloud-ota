package com.coffee_is_essential.iot_cloud_ota.service;

import com.coffee_is_essential.iot_cloud_ota.dto.DeviceResponseDto;
import com.coffee_is_essential.iot_cloud_ota.repository.DeviceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeviceService {
    private final DeviceJpaRepository deviceJpaRepository;

    public DeviceResponseDto saveDevice() {
        return null;
    }
}
