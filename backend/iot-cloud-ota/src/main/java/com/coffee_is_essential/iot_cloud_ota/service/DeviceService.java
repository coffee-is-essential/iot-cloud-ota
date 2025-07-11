package com.coffee_is_essential.iot_cloud_ota.service;

import com.coffee_is_essential.iot_cloud_ota.dto.DeviceSummaryResponseDto;
import com.coffee_is_essential.iot_cloud_ota.entity.Device;
import com.coffee_is_essential.iot_cloud_ota.entity.Division;
import com.coffee_is_essential.iot_cloud_ota.entity.Region;
import com.coffee_is_essential.iot_cloud_ota.repository.DeviceJpaRepository;
import com.coffee_is_essential.iot_cloud_ota.repository.DivisionJpaRepository;
import com.coffee_is_essential.iot_cloud_ota.repository.RegionJpaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

    /**
     * 디바이스 요약 정보 목록을 조회합니다.
     * 디바이스 ID, 디바이스 이름, 리전 이름, 그룹 이름, 활성 상태을 포함한 요약 데이터를 DTO 형태로 반환합니다.
     *
     * @return DeviceSummaryResponseDto 리스트 (deviceID, deviceName, regionName, groupName, isActive)
     */
    public List<DeviceSummaryResponseDto> findDeviceSummary() {

        return deviceJpaRepository.findDeviceSummary()
                .stream()
                .map(DeviceSummaryResponseDto::from)
                .toList();
    }
}
