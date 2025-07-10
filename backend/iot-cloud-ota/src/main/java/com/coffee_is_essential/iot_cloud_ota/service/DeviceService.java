package com.coffee_is_essential.iot_cloud_ota.service;

import com.coffee_is_essential.iot_cloud_ota.entity.Device;
import com.coffee_is_essential.iot_cloud_ota.entity.Division;
import com.coffee_is_essential.iot_cloud_ota.entity.Region;
import com.coffee_is_essential.iot_cloud_ota.repository.DeviceJpaRepository;
import com.coffee_is_essential.iot_cloud_ota.repository.DivisionJpaRepository;
import com.coffee_is_essential.iot_cloud_ota.repository.RegionJpaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DeviceService {
    private final RegionJpaRepository regionJpaRepository;
    private final DivisionJpaRepository divisionJpaRepository;
    private final DeviceJpaRepository deviceJpaRepository;

    /**
     * 새로운 디바이스를 생성하고 저장합니다.
     *
     * @param deviceName 디바이스 이름
     * @param regionId   연결할 리전의 ID
     * @param divisionId 연결할 디비전의 ID
     */
    public void saveDevice(String deviceName, Long regionId, Long divisionId) {
        Region region = regionJpaRepository.findByIdOrElseThrow(regionId);
        Division division = divisionJpaRepository.findByIdOrElseThrow(divisionId);
        Device device = new Device(deviceName, division, region);

        deviceJpaRepository.save(device);
    }
}
