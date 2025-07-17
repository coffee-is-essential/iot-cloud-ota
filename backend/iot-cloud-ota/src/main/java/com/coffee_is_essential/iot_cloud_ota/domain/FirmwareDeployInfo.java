package com.coffee_is_essential.iot_cloud_ota.domain;

import com.coffee_is_essential.iot_cloud_ota.entity.FirmwareMetadata;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

/**
 * 펌웨어 배포 작업의 메타 정보를 담는 불변 객체입니다.
 *
 * @param deploymentId 배포 고유 ID (UUID)
 * @param version      배포할 펌웨어 버전
 * @param fileHash     파일의 SHA-256 해시
 * @param fileSize     파일 크기 (바이트)
 * @param expiresAt    만료시간 (예: 10분)
 * @param deployedAt   배포 시작 시간
 */
public record FirmwareDeployInfo(
        String deploymentId,
        String version,
        String fileHash,
        long fileSize,
        Date expiresAt,
        LocalDateTime deployedAt
) {
    public static FirmwareDeployInfo from(FirmwareMetadata firmwareMetadata, Date expiresAt) {
        return new FirmwareDeployInfo(
                UUID.randomUUID().toString(),
                firmwareMetadata.getVersion(),
                firmwareMetadata.getFileHash(),
                firmwareMetadata.getFileSize(),
                expiresAt,
                LocalDateTime.now()
        );
    }
}
