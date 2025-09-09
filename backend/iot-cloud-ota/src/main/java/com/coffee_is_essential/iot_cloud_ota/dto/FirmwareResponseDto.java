package com.coffee_is_essential.iot_cloud_ota.dto;

import com.coffee_is_essential.iot_cloud_ota.entity.DeviceFirmware;

public record FirmwareResponseDto(
        Long id,
        String version
) {
    public static FirmwareResponseDto from(DeviceFirmware deviceFirmware) {
        return new FirmwareResponseDto(
                deviceFirmware.getFirmware().getId(),
                deviceFirmware.getFirmware().getVersion()
        );
    }
}
