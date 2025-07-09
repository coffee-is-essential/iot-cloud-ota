package com.coffee_is_essential.iot_cloud_ota.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.coffee_is_essential.iot_cloud_ota.dto.UploadPresignedUrlResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
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

    /**
     * 지정한 버전과 파일 이름을 기반으로 S3에 업로드할 수 있는 Presigned URL을 생성합니다.
     *
     * @param version  파일 버전 (예: "v1.0.0")
     * @param fileName 저장할 파일 이름 (예: "firmware.zip")
     * @return 업로드용 Presigned URL 및 S3 저장 경로가 포함된 DTO
     */
    public UploadPresignedUrlResponseDto getPresignedUrl(String version, String fileName) {
        String path = createPath(version, fileName);

        GeneratePresignedUrlRequest generatePresignedUrlRequest = generatePresignedUrlRequest(bucketName, path);
        String url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest).toString();

        return new UploadPresignedUrlResponseDto(url, path);
    }

    /**
     * Presigned URL 생성을 위한 요청 객체를 구성합니다.
     *
     * @param bucket 대상 S3 버킷 이름
     * @param path   S3 내 저장될 경로
     * @return Presigned URL 생성 요청 객체
     */
    private GeneratePresignedUrlRequest generatePresignedUrlRequest(String bucket, String path) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucket, path)
                        .withMethod(HttpMethod.PUT)
                        .withExpiration(getPresignedUrlExpiration());

        return generatePresignedUrlRequest;
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
