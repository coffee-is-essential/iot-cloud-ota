package com.coffee_is_essential.iot_cloud_ota.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Date;

/**
 * S3Service 클래스는 AWS S3 Presigned URL 발급 기능을 제공합니다.
 * 이를 통해 클라이언트가 인증 없이 제한 시간 동안 S3에 직접 업로드할 수 있습니다.
 */
@Service
@RequiredArgsConstructor
public class S3Service {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;

    /**
     * 지정한 prefix와 파일명으로 S3 Presigned URL을 생성합니다.
     *
     * @param prefix   업로드 경로에 사용할 접두사 (예: "v1.0.0")
     * @param fileName 저장할 파일 이름
     * @return Presigned URL 문자열 (PUT 요청용)
     */
    public String getPresignedUrl(String prefix, String fileName) {
        String path = createPath(prefix, fileName);

        GeneratePresignedUrlRequest generatePresignedUrlRequest = generatePresignedUrlRequest(bucket, path);
        URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);

        return url.toString();
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
     * 기본 유효 시간은 현재 시간으로부터 2분입니다.
     *
     * @return 만료 시간 (Date 객체)
     */
    private Date getPresignedUrlExpiration() {
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 2;
        expiration.setTime(expTimeMillis);

        return expiration;
    }

    /**
     * prefix와 파일명을 결합하여 S3 경로를 생성합니다.
     *
     * @param prefix   경로 접두사
     * @param fileName 파일 이름
     * @return 결합된 경로 문자열 (예: "v1.0.0/firmware.zip")
     */
    private String createPath(String prefix, String fileName) {

        return String.format("%s/%s", prefix, fileName);
    }
}
