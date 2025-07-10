package com.coffee_is_essential.iot_cloud_ota.component;

import com.coffee_is_essential.iot_cloud_ota.entity.Device;
import com.coffee_is_essential.iot_cloud_ota.entity.FirmwareMetadata;
import com.coffee_is_essential.iot_cloud_ota.entity.Division;
import com.coffee_is_essential.iot_cloud_ota.entity.Region;
import com.coffee_is_essential.iot_cloud_ota.repository.DeviceJpaRepository;
import com.coffee_is_essential.iot_cloud_ota.repository.FirmwareMetadataJpaRepository;
import com.coffee_is_essential.iot_cloud_ota.repository.GroupJpaRepository;
import com.coffee_is_essential.iot_cloud_ota.repository.RegionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 애플리케이션 시작 시 테스트용 펌웨어 메타데이터를 DB에 삽입하는 컴포넌트입니다.
 */
@Component
@RequiredArgsConstructor
public class MockDataInitializer implements CommandLineRunner {
    private final FirmwareMetadataJpaRepository firmwareMetadataJpaRepository;
    private final RegionJpaRepository regionJpaRepository;
    private final GroupJpaRepository groupJpaRepository;
    private final DeviceJpaRepository deviceJpaRepository;

    @Override
    public void run(String... args) throws Exception {
        saveFirmwareMetadata();
        saveRegion();
        saveGroup();
        saveDevice();
    }

    private void saveFirmwareMetadata() {
        firmwareMetadataJpaRepository.save(new FirmwareMetadata("24.04.01", "24.04.01.ino", "광고가 표시되지 않는 버그를 수정했습니다.", "24.04.01/uuid1/24.04.01.ino"));
        firmwareMetadataJpaRepository.save(new FirmwareMetadata("24.04.02", "24.04.02.ino", "원두 선택 버튼이 클릭되지 않는 버그를 수정했습니다", "24.04.02/uuid2/24.04.02.ino"));
        firmwareMetadataJpaRepository.save(new FirmwareMetadata("24.04.03", "24.04.03.ino", "연두해요 연두 광고를 업로드하였습니다.", "24.04.03/uuid3/24.04.03.ino"));
        firmwareMetadataJpaRepository.save(new FirmwareMetadata("24.04.04", "24.04.04.ino", "동원참치 캔 광고가 표시되지 않는 버그를 수정하였습니다.", "24.04.04/uuid4/24.04.04.ino"));

        firmwareMetadataJpaRepository.save(new FirmwareMetadata("24.04.05", "24.04.05.ino", "광고가 표시되지 않는 버그를 수정했습니다.", "24.04.05/uuid5/24.04.05.ino"));
        firmwareMetadataJpaRepository.save(new FirmwareMetadata("24.04.06", "24.04.06.ino", "원두 선택 버튼이 클릭되지 않는 버그를 수정했습니다", "24.04.06/uuid6/24.04.06.ino"));
        firmwareMetadataJpaRepository.save(new FirmwareMetadata("24.04.07", "24.04.07.ino", "연두해요 연두 광고를 업로드하였습니다.", "24.04.07/uuid7/24.04.07.ino"));
        firmwareMetadataJpaRepository.save(new FirmwareMetadata("24.04.08", "24.04.08.ino", "동원참치 캔 광고가 표시되지 않는 버그를 수정하였습니다.", "24.04.08/uuid8/24.04.08.ino"));

        firmwareMetadataJpaRepository.save(new FirmwareMetadata("24.04.09", "24.04.09.ino", "광고가 표시되지 않는 버그를 수정했습니다.", "24.04.09/uuid9/24.04.09.ino"));
        firmwareMetadataJpaRepository.save(new FirmwareMetadata("24.04.10", "24.04.10.ino", "원두 선택 버튼이 클릭되지 않는 버그를 수정했습니다", "24.04.10/uuid10/24.04.10.ino"));
        firmwareMetadataJpaRepository.save(new FirmwareMetadata("24.04.11", "24.04.11.ino", "연두해요 연두 광고를 업로드하였습니다.", "24.04.11/uuid11/24.04.11.ino"));
        firmwareMetadataJpaRepository.save(new FirmwareMetadata("24.04.12", "24.04.12.ino", "동원참치 캔 광고가 표시되지 않는 버그를 수정하였습니다.", "24.04.12/uuid12/24.04.12.ino"));
    }

    private void saveRegion() {
        regionJpaRepository.save(new Region("us-west-1", "미국 북부 캘리포니아"));
        regionJpaRepository.save(new Region("us-east-1", "미국 북부 버지니아"));
        regionJpaRepository.save(new Region("ap-south-1", "인도 뭄바이"));
        regionJpaRepository.save(new Region("ap-northeast-2", "서울"));
        regionJpaRepository.save(new Region("ap-northeast-3", "오사카"));
        regionJpaRepository.save(new Region("ap-west-1", "인도"));
        regionJpaRepository.save(new Region("ap-southeast-3", "자카르타"));
    }

    private void saveGroup() {
        groupJpaRepository.save(new Division("dmeowk-203", "서울 모수"));
        groupJpaRepository.save(new Division("wmeotc-391", "부산 스타벅스 광안리점"));
        groupJpaRepository.save(new Division("wjrpvo-100", "오꾸닭 신림점"));
        groupJpaRepository.save(new Division("woeprz-009", "네이버 판교 본사"));
    }

    private void saveDevice() {
        deviceJpaRepository.save(new Device("bartooler-001", null, null));
        deviceJpaRepository.save(new Device("bartooler-002", null, null));
        deviceJpaRepository.save(new Device("bartooler-003", null, null));
        deviceJpaRepository.save(new Device("bartooler-004", null, null));
        deviceJpaRepository.save(new Device("bartooler-005", null, null));
        deviceJpaRepository.save(new Device("bartooler-006", null, null));
        deviceJpaRepository.save(new Device("bartooler-007", null, null));
    }
}
