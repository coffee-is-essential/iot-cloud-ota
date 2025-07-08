package com.coffee_is_essential.iot_cloud_ota.component;

import com.coffee_is_essential.iot_cloud_ota.entity.FirmwareMetadata;
import com.coffee_is_essential.iot_cloud_ota.repository.FirmwareMetadataJpaRepository;
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

    @Override
    public void run(String... args) throws Exception {
        firmwareMetadataJpaRepository.save(new FirmwareMetadata("24.04.01", "24.04.01.ino", "광고가 표시되지 않는 버그를 수정했습니다."));
        firmwareMetadataJpaRepository.save(new FirmwareMetadata("24.04.02", "24.04.02.ino", "원두 선택 버튼이 클릭되지 않는 버그를 수정했습니다"));
        firmwareMetadataJpaRepository.save(new FirmwareMetadata("24.04.03", "24.04.03.ino", "연두해요 연두 광고를 업로드하였습니다."));
        firmwareMetadataJpaRepository.save(new FirmwareMetadata("24.04.04", "24.04.04.ino", "동원참치 캔 광고가 표시되지 않는 버그를 수정하였습니다."));

        firmwareMetadataJpaRepository.save(new FirmwareMetadata("24.04.05", "24.04.05.ino", "광고가 표시되지 않는 버그를 수정했습니다."));
        firmwareMetadataJpaRepository.save(new FirmwareMetadata("24.04.06", "24.04.06.ino", "원두 선택 버튼이 클릭되지 않는 버그를 수정했습니다"));
        firmwareMetadataJpaRepository.save(new FirmwareMetadata("24.04.07", "24.04.07.ino", "연두해요 연두 광고를 업로드하였습니다."));
        firmwareMetadataJpaRepository.save(new FirmwareMetadata("24.04.08", "24.04.08.ino", "동원참치 캔 광고가 표시되지 않는 버그를 수정하였습니다."));

        firmwareMetadataJpaRepository.save(new FirmwareMetadata("24.04.09", "24.04.09.ino", "광고가 표시되지 않는 버그를 수정했습니다."));
        firmwareMetadataJpaRepository.save(new FirmwareMetadata("24.04.10", "24.04.10.ino", "원두 선택 버튼이 클릭되지 않는 버그를 수정했습니다"));
        firmwareMetadataJpaRepository.save(new FirmwareMetadata("24.04.11", "24.04.11.ino", "연두해요 연두 광고를 업로드하였습니다."));
        firmwareMetadataJpaRepository.save(new FirmwareMetadata("24.04.12", "24.04.12.ino", "동원참치 캔 광고가 표시되지 않는 버그를 수정하였습니다."));
    }
}
