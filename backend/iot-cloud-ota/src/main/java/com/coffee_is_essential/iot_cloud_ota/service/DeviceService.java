package com.coffee_is_essential.iot_cloud_ota.service;

import com.coffee_is_essential.iot_cloud_ota.dto.DeviceSummaryResponseDto;
import com.coffee_is_essential.iot_cloud_ota.entity.*;
import com.coffee_is_essential.iot_cloud_ota.repository.*;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class DeviceService {
    private final RegionJpaRepository regionJpaRepository;
    private final DivisionJpaRepository divisionJpaRepository;
    private final DeviceJpaRepository deviceJpaRepository;
    private final AdsMetadataJpaRepository adsMetadataJpaRepository;
    private final EntityManager em;
    private final FirmwareDeploymentRepository firmwareDeploymentRepository;

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

    /**
     * 특정 commandId에 대해 완료된 펌웨어 다운로드 이벤트를 기반으로
     * 각 디바이스의 광고 상태(DeviceAds)를 업데이트합니다.
     * 기존에 활성화된 광고는 종료 처리하고, 새로운 광고 메타데이터를 기반으로
     * 새로운 광고 상태를 생성하여 저장합니다.
     *
     * @param commandId       펌웨어 배포 명령 식별자
     * @param completedEvents 완료된 펌웨어 다운로드 이벤트 리스트
     */
    @Transactional
    public void updateDeviceAds(String commandId, List<FirmwareDownloadEvents> completedEvents) {
        List<AdsMetadata> metadataList = adsMetadataJpaRepository.findByCommandId(commandId);
        OffsetDateTime now = OffsetDateTime.now();

        for (FirmwareDownloadEvents event : completedEvents) {
            Long deviceId = event.getDeviceId();

            em.createQuery("""
                                UPDATE DeviceAds da
                                SET da.endedAt = :now
                                WHERE da.device.id = :deviceId
                                  AND da.endedAt IS NULL
                            """)
                    .setParameter("now", now)
                    .setParameter("deviceId", deviceId)
                    .executeUpdate();

            for (AdsMetadata ads : metadataList) {
                DeviceAds newAds = new DeviceAds(
                        em.getReference(Device.class, deviceId),
                        ads,
                        now
                );
                em.persist(newAds);
            }
        }
    }

    /**
     * 특정 commandId에 대해 완료된 펌웨어 다운로드 이벤트를 기반으로
     * 각 디바이스의 펌웨어 상태(DeviceFirmware)를 업데이트합니다.
     * 기존에 활성화된 펌웨어는 종료 처리하고, 새로운 펌웨어 메타데이터를 기반으로
     * 새로운 펌웨어 상태를 생성하여 저장합니다.
     *
     * @param commandId       펌웨어 배포 명령 식별자
     * @param completedEvents 완료된 펌웨어 다운로드 이벤트 리스트
     */
    @Transactional
    public void updateDeviceFirmware(String commandId, List<FirmwareDownloadEvents> completedEvents) {
        FirmwareDeployment deployment = firmwareDeploymentRepository.findByCommandIdOrElseThrow(commandId);
        FirmwareMetadata firmware = deployment.getFirmwareMetadata();
        OffsetDateTime now = OffsetDateTime.now();

        for (FirmwareDownloadEvents event : completedEvents) {
            Long deviceId = event.getDeviceId();

            em.createQuery("""
                                UPDATE DeviceFirmware df
                                SET df.endedAt = :now
                                WHERE df.device.id = :deviceId
                                  AND df.endedAt IS NULL
                            """)
                    .setParameter("now", now)
                    .setParameter("deviceId", deviceId)
                    .executeUpdate();

            DeviceFirmware newFirmware = new DeviceFirmware(
                    em.getReference(Device.class, deviceId),
                    firmware,
                    now
            );
            em.persist(newFirmware);
        }
    }
}
