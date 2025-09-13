package com.coffee_is_essential.iot_cloud_ota.service;

import com.coffee_is_essential.iot_cloud_ota.domain.PaginationInfo;
import com.coffee_is_essential.iot_cloud_ota.dto.*;
import com.coffee_is_essential.iot_cloud_ota.entity.*;
import com.coffee_is_essential.iot_cloud_ota.repository.*;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class DeviceService {
    private final RegionJpaRepository regionJpaRepository;
    private final DivisionJpaRepository divisionJpaRepository;
    private final DeviceJpaRepository deviceJpaRepository;
    private final AdsMetadataJpaRepository adsMetadataJpaRepository;
    private final EntityManager em;
    private final FirmwareDeploymentRepository firmwareDeploymentRepository;
    private final DeviceStatusJdbcRepository deviceStatusJdbcRepository;
    private final DeviceFirmwareJpaRepository deviceFirmwareJpaRepository;
    private final DeviceAdsJpaRepository deviceAdsJpaRepository;
    private final CloudFrontSignedUrlService cloudFrontSignedUrlService;

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
     * 모든 디바이스의 요약 정보를 조회합니다.
     * 각 디바이스에 대해 최신 시스템 상태를 확인하여 활성 상태를 판단합니다.
     *
     * @return 디바이스 요약 정보 리스트
     */
    public List<DeviceSummaryResponseDto> findDeviceSummary() {

        return deviceJpaRepository.findDeviceSummary().stream()
                .map(ds -> {
                    SystemStatus status = deviceStatusJdbcRepository.findLatestByDeviceId(ds.getDeviceId());
                    boolean isActive = status != null && status.getTimestamp().isAfter(OffsetDateTime.now().minusMinutes(5));
                    return new DeviceSummaryResponseDto(
                            ds.getDeviceId(),
                            ds.getDeviceName(),
                            ds.getRegionName(),
                            ds.getGroupName(),
                            isActive
                    );
                })
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

    /**
     * 특정 리전과 그룹에 속한 디바이스 목록을 페이지네이션하여 조회합니다.
     * 각 디바이스에 대해 최신 시스템 상태의 타임스탬프를 포함한 정보를 DTO 형태로 반환합니다.
     *
     * @param regionId       조회할 리전의 ID
     * @param groupId        조회할 그룹의 ID
     * @param paginationInfo 페이지네이션 정보 (페이지 번호, 페이지 크기 등)
     * @return DeviceListResponseDto (디바이스 목록과 페이지 메타데이터)
     */
    public DeviceListResponseDto findAllDevices(Long regionId, Long groupId, PaginationInfo paginationInfo) {
        Pageable pageable = PageRequest.of(
                paginationInfo.page() - 1,
                paginationInfo.limit(),
                Sort.by("createdAt").ascending().and(Sort.by("id").ascending())
        );
        Page<Device> devicesPage = deviceJpaRepository.findByRegionAndGroup(regionId, groupId, pageable);
        List<Device> content = devicesPage.getContent();

        List<DeviceResponseDto> deviceResponseDtos = content.stream()
                .map(d -> {
                    SystemStatus status = deviceStatusJdbcRepository.findLatestByDeviceId(d.getDeviceId());
                    return new DeviceResponseDto(
                            d.getDeviceId(),
                            d.getName(),
                            d.getCreatedAt(),
                            d.getRegion().getRegionName(),
                            d.getDivision().getDivisionName(),
                            status != null ? status.getTimestamp() : null
                    );
                })
                .toList();

        return DeviceListResponseDto.of(deviceResponseDtos, PaginationMetadataDto.from(devicesPage));
    }

    /**
     * 특정 디바이스의 상세 정보를 조회합니다.
     * 디바이스의 기본 정보, 최신 시스템 상태, 현재 활성화된 펌웨어 및 광고 정보를 포함한 상세 데이터를 DTO 형태로 반환합니다.
     *
     * @param id 조회할 디바이스의 ID
     * @return DeviceDetailResponseDto (디바이스 상세 정보)
     */
    public DeviceDetailResponseDto findDetailByDeviceId(Long id) {
        Device device = deviceJpaRepository.findByIdOrElseThrow(id);
        SystemStatus status = deviceStatusJdbcRepository.findLatestByDeviceId(device.getDeviceId());
        Optional<DeviceFirmware> deviceFirmware = deviceFirmwareJpaRepository.findByDeviceIdAndEndedAtIsNull(device.getDeviceId());
        List<DeviceAds> deviceAds = deviceAdsJpaRepository.findByDeviceIdAndEndedAtIsNull(device.getDeviceId());

        return new DeviceDetailResponseDto(
                device.getDeviceId(),
                device.getName(),
                device.getCreatedAt(),
                device.getModifiedAt(),
                status != null ? status.getTimestamp() : null,
                status != null && status.getTimestamp().isAfter(OffsetDateTime.now().minusMinutes(5)),

                DeviceDetailRegionDto.from(device.getRegion()),
                DeviceDetailDivisionDto.from(device.getDivision()),
                deviceFirmware.map(FirmwareResponseDto::from).orElse(null),
                deviceAds.stream()
                        .map(da ->
                                new AdsResponseDto(
                                        da.getAdsMetadata().getId(),
                                        da.getAdsMetadata().getTitle(),
                                        da.getStartedAt(),
                                        cloudFrontSignedUrlService.generateAdsSignedUrl(da.getAdsMetadata().getTitle()).url()
                                )
                        )
                        .toList()
        );
    }
}
