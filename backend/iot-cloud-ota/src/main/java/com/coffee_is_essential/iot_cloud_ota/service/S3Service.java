package com.coffee_is_essential.iot_cloud_ota.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.coffee_is_essential.iot_cloud_ota.dto.DownloadPresignedUrlResponseDto;
import com.coffee_is_essential.iot_cloud_ota.dto.UploadPresignedUrlResponseDto;
import com.coffee_is_essential.iot_cloud_ota.entity.FirmwareMetadata;
import com.coffee_is_essential.iot_cloud_ota.repository.FirmwareMetadataJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

/**
 * S3Service 클래스는 AWS S3 Presigned URL 발급 기능을 제공합니다.
 * 이를 통해 클라이언트가 인증 없이 제한 시간 동안 S3에 직접 업로드할 수 있습니다.
 */
@Service
@RequiredArgsConstructor
public class S3Service {
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private final AmazonS3 amazonS3;
    private final FirmwareMetadataJpaRepository firmwareMetadataJpaRepository;

    /**
     * 지정한 버전과 파일 이름을 기반으로 S3에 업로드할 수 있는 Presigned URL을 생성합니다.
     *
     * @param version  파일 버전 (예: "v1.0.0")
     * @param fileName 저장할 파일 이름 (예: "firmware.zip")
     * @return 업로드용 Presigned URL 및 S3 저장 경로가 포함된 DTO
     */
    public UploadPresignedUrlResponseDto getPresignedUploadUrl(String version, String fileName) {
        String path = createPath(version, fileName);
        GeneratePresignedUrlRequest generatedPresignedUrlRequest = generatePresignedUploadUrl(bucketName, path);
        String url = amazonS3.generatePresignedUrl(generatedPresignedUrlRequest).toString();

        return new UploadPresignedUrlResponseDto(url, path);
    }

    /**
     * 지정된 S3 경로를 기반으로 업로드용 Presigned URL 요청 객체를 생성합니다.
     *
     * @param bucket 대상 S3 버킷 이름
     * @param path   S3 내 저장될 경로
     * @return 업로드용 Presigned URL 요청 객체
     */
    private GeneratePresignedUrlRequest generatePresignedUploadUrl(String bucket, String path) {
        GeneratePresignedUrlRequest request =
                new GeneratePresignedUrlRequest(bucket, path)
                        .withMethod(HttpMethod.PUT)
                        .withExpiration(getPresignedUrlExpiration());

        return request;
    }

    /**
     * 지정된 펌웨어 ID에 해당하는 S3 객체에 대한 Presigned 다운로드 URL을 생성합니다.
     *
     * @param id 펌웨어 메터데이터의 고유 ID
     * @return 다운로드용 Presigned URL을 포함한 DTO
     */
    public DownloadPresignedUrlResponseDto getPresignedDownloadUrl(Long id) {
        FirmwareMetadata metadata = firmwareMetadataJpaRepository.findByIdOrElseThrow(id);
        GeneratePresignedUrlRequest generatedPresignedUrlRequest = generatePresignedDownloadUrl(bucketName, metadata.getS3Path());
        String url = amazonS3.generatePresignedUrl(generatedPresignedUrlRequest).toString();

        return new DownloadPresignedUrlResponseDto(url);
    }

    /**
     * 지정된 S3 경로를 기반으로 다운로드용 Presigned URL 요청 객체를 생성합니다.
     *
     * @param bucket 대상 S3 버킷 이름
     * @param path   다운로드할 S3 객체의 경로
     * @return 다운로드용 Presigned URL 요청 객체
     */
    private GeneratePresignedUrlRequest generatePresignedDownloadUrl(String bucket, String path) {
        GeneratePresignedUrlRequest request =
                new GeneratePresignedUrlRequest(bucket, path)
                        .withMethod(HttpMethod.GET)
                        .withExpiration(getPresignedUrlExpiration());

        return request;
    }

    /**
     * Presigned URL의 만료 시간을 반환합니다.
     * 기본 유효 시간은 현재 시간으로부터 5분입니다.
     *
     * @return 만료 시간 (Date 객체)
     */
    private Date getPresignedUrlExpiration() {
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 5;
        expiration.setTime(expTimeMillis);

        return expiration;
    }

    /**
     * 파일버전, UUID, 파일명을 결합하여 S3 경로를 생성합니다.
     *
     * @param version  파일 버전
     * @param fileName 파일 이름
     * @return 결합된 경로 문자열 (예: "v1.0.0/uuid/firmware.zip")
     */
    private String createPath(String version, String fileName) {
        String uuid = UUID.randomUUID().toString();

        return String.format("%s/%s/%s", version, uuid, fileName);
    }
}
