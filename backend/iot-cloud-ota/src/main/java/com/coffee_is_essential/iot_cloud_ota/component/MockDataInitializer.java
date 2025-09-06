//package com.coffee_is_essential.iot_cloud_ota.component;
//
//import com.coffee_is_essential.iot_cloud_ota.entity.*;
//import com.coffee_is_essential.iot_cloud_ota.repository.*;
//import com.coffee_is_essential.iot_cloud_ota.service.DeviceService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.time.OffsetDateTime;
//import java.util.List;
//
///**
// * 애플리케이션 시작 시 테스트용 펌웨어 메타데이터를 DB에 삽입하는 컴포넌트입니다.
// */
//@Component
//@RequiredArgsConstructor
//public class MockDataInitializer implements CommandLineRunner {
//    private final FirmwareMetadataJpaRepository firmwareMetadataJpaRepository;
//    private final RegionJpaRepository regionJpaRepository;
//    private final DivisionJpaRepository divisionJpaRepository;
//    private final FirmwareMetadataJpaRepository firmwareMetadataRepository;
//    private final DeviceAdsJpaRepository deviceAdsJpaRepository;
//    private final DeviceService deviceService;
//    private final AdsMetadataJpaRepository adsMetadataJpaRepository;
//    private final DeviceJpaRepository deviceJpaRepository;
//
//    @Override
//    public void run(String... args) throws Exception {
//        saveFirmwareMetadata();
//        saveRegion();
//        saveGroup();
//        saveDevice();
//        saveDeviceAds();
//    }
//
//    private void saveFirmwareMetadata() {
//        firmwareMetadataJpaRepository.save(new FirmwareMetadata("24.04.01", "24.04.01.ino", "광고가 표시되지 않는 버그를 수정했습니다.", "24.04.01/uuid1/24.04.01.ino", "hash123124", 123L));
//        firmwareMetadataJpaRepository.save(new FirmwareMetadata("24.04.02", "24.04.02.ino", "원두 선택 버튼이 클릭되지 않는 버그를 수정했습니다", "24.04.02/uuid2/24.04.02.ino", "hash123124", 123L));
//        firmwareMetadataJpaRepository.save(new FirmwareMetadata("24.04.03", "24.04.03.ino", "연두해요 연두 광고를 업로드하였습니다.", "24.04.03/uuid3/24.04.03.ino", "hash123124", 123L));
//        firmwareMetadataJpaRepository.save(new FirmwareMetadata("24.04.04", "24.04.04.ino", "동원참치 캔 광고가 표시되지 않는 버그를 수정하였습니다.", "24.04.04/uuid4/24.04.04.ino", "hash123124", 123L));
//
//        firmwareMetadataJpaRepository.save(new FirmwareMetadata("24.04.05", "24.04.05.ino", "광고가 표시되지 않는 버그를 수정했습니다.", "24.04.05/uuid5/24.04.05.ino", "hash123124", 123L));
//        firmwareMetadataJpaRepository.save(new FirmwareMetadata("24.04.06", "24.04.06.ino", "원두 선택 버튼이 클릭되지 않는 버그를 수정했습니다", "24.04.06/uuid6/24.04.06.ino", "hash123124", 123L));
//        firmwareMetadataJpaRepository.save(new FirmwareMetadata("24.04.07", "24.04.07.ino", "연두해요 연두 광고를 업로드하였습니다.", "24.04.07/uuid7/24.04.07.ino", "hash123124", 123L));
//        firmwareMetadataJpaRepository.save(new FirmwareMetadata("24.04.08", "24.04.08.ino", "동원참치 캔 광고가 표시되지 않는 버그를 수정하였습니다.", "24.04.08/uuid8/24.04.08.ino", "hash123124", 123L));
//
//        firmwareMetadataJpaRepository.save(new FirmwareMetadata("24.04.09", "24.04.09.ino", "광고가 표시되지 않는 버그를 수정했습니다.", "24.04.09/uuid9/24.04.09.ino", "hash123124", 123L));
//        firmwareMetadataJpaRepository.save(new FirmwareMetadata("24.04.10", "24.04.10.ino", "원두 선택 버튼이 클릭되지 않는 버그를 수정했습니다", "24.04.10/uuid10/24.04.10.ino", "hash123124", 123L));
//        firmwareMetadataJpaRepository.save(new FirmwareMetadata("24.04.11", "24.04.11.ino", "연두해요 연두 광고를 업로드하였습니다.", "24.04.11/uuid11/24.04.11.ino", "hash123124", 123L));
//        firmwareMetadataJpaRepository.save(new FirmwareMetadata("24.04.12", "24.04.12.ino", "동원참치 캔 광고가 표시되지 않는 버그를 수정하였습니다.", "24.04.12/uuid12/24.04.12.ino", "hash123124", 123L));
//    }
//
//    private void saveRegion() {
//        regionJpaRepository.save(new Region("us-west-1", "미국 북부 캘리포니아"));
//        regionJpaRepository.save(new Region("us-east-1", "미국 북부 버지니아"));
//        regionJpaRepository.save(new Region("ap-south-1", "인도 뭄바이"));
//        regionJpaRepository.save(new Region("ap-northeast-2", "서울"));
//        regionJpaRepository.save(new Region("ap-northeast-3", "오사카"));
//        regionJpaRepository.save(new Region("ap-west-1", "인도"));
//        regionJpaRepository.save(new Region("ap-southeast-3", "자카르타"));
//    }
//
//    private void saveGroup() {
//        divisionJpaRepository.save(new Division("dmeowk-203", "서울 모수"));
//        divisionJpaRepository.save(new Division("wmeotc-391", "부산 스타벅스 광안리점"));
//        divisionJpaRepository.save(new Division("wjrpvo-100", "오꾸닭 신림점"));
//        divisionJpaRepository.save(new Division("woeprz-009", "네이버 판교 본사"));
//    }
//
//    private void saveDevice() {
//        deviceService.saveDevice("bartooler-001", 1L, 1L);
//        deviceService.saveDevice("bartooler-002", 1L, 1L);
//        deviceService.saveDevice("bartooler-003", 4L, 1L);
//        deviceService.saveDevice("bartooler-004", 3L, 3L);
//        deviceService.saveDevice("bartooler-005", 3L, 3L);
//        deviceService.saveDevice("bartooler-006", 3L, 3L);
//        deviceService.saveDevice("bartooler-007", 3L, 3L);
//    }
//
//    private void saveDeviceAds() {
//        List<AdsMetadata> list = adsMetadataJpaRepository.findAll();
//        Device device1 = deviceJpaRepository.findById(1L).get();
//        Device device2 = deviceJpaRepository.findById(2L).get();
//        List<DeviceAds> deviceAds = List.of(
//                new DeviceAds(
//                        device1,
//                        list.get(1),
//                        OffsetDateTime.now(),
//                        null,
//                        true
//                ),
//                new DeviceAds(
//                        device1,
//                        list.get(2),
//                        OffsetDateTime.now(),
//                        null,
//                        true
//                ),
//                new DeviceAds(
//                        device1,
//                        list.get(3),
//                        OffsetDateTime.now(),
//                        null,
//                        true
//                ),
//                new DeviceAds(
//                        device1,
//                        list.get(4),
//                        OffsetDateTime.now(),
//                        OffsetDateTime.now(),
//                        false
//                ),
//
//
//                new DeviceAds(
//                        device2,
//                        list.get(2),
//                        OffsetDateTime.now(),
//                        null,
//                        true
//                ),
//                new DeviceAds(
//                        device2,
//                        list.get(3),
//                        OffsetDateTime.now(),
//                        null,
//                        true
//                ),
//                new DeviceAds(
//                        device2,
//                        list.get(4),
//                        OffsetDateTime.now(),
//                        null,
//                        true
//                )
//        );
//
//        deviceAdsJpaRepository.saveAll(deviceAds);
//    }
//}
