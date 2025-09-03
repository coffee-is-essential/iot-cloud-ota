package com.coffee_is_essential.iot_cloud_ota.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.coffee_is_essential.iot_cloud_ota.domain.S3FileHashResult;
import com.coffee_is_essential.iot_cloud_ota.dto.AdsUploadPresignedUrlResponseDto;
import com.coffee_is_essential.iot_cloud_ota.dto.DownloadPresignedUrlResponseDto;
import com.coffee_is_essential.iot_cloud_ota.dto.UploadPresignedUrlResponseDto;
import com.coffee_is_essential.iot_cloud_ota.entity.FirmwareMetadata;
import com.coffee_is_essential.iot_cloud_ota.repository.AdvertisementMetadataJpaRepository;
import com.coffee_is_essential.iot_cloud_ota.repository.FirmwareMetadataJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Optional;
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
    private final AdvertisementMetadataJpaRepository advertisementMetadataJpaRepository;

    /**
     * 지정한 버전과 파일 이름을 기반으로 S3에 업로드할 수 있는 Presigned URL을 생성합니다.
     *
     * @param version  파일 버전 (예: "v1.0.0")
     * @param fileName 저장할 파일 이름 (예: "firmware.zip")
     * @return 업로드용 Presigned URL 및 S3 저장 경로가 포함된 DTO
     */
    @Transactional
    public UploadPresignedUrlResponseDto getPresignedUploadUrl(String version, String fileName) {
        if (firmwareMetadataJpaRepository.findByVersionAndFileName(version, fileName).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 버전과 파일 이름의 펌웨어가 이미 존재합니다.");
        }

        String path = createPath(version, fileName);
        GeneratePresignedUrlRequest generatedPresignedUrlRequest = generatePresignedUploadUrl(bucketName, path);
        String url = amazonS3.generatePresignedUrl(generatedPresignedUrlRequest).toString();

        return new UploadPresignedUrlResponseDto(url, path);
    }

    @Transactional
    public AdsUploadPresignedUrlResponseDto getAdsPresignedUploadUrl(String title) {
        if (advertisementMetadataJpaRepository.findByTitle(title).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 제목의 광고가 이미 존재합니다.");
        }

        String originalPath = createOriginalFilePath(title);
        String binaryPath = createBinaryFilePath(title);
        GeneratePresignedUrlRequest originUrlRequest = generatePresignedUploadUrl(bucketName, originalPath);
        GeneratePresignedUrlRequest binaryUrlRequest = generatePresignedUploadUrl(bucketName, binaryPath);

        String originalUrl = amazonS3.generatePresignedUrl(originUrlRequest).toString();
        String binaryUrl = amazonS3.generatePresignedUrl(binaryUrlRequest).toString();

        UploadPresignedUrlResponseDto original = new UploadPresignedUrlResponseDto(originalUrl, originalPath);
        UploadPresignedUrlResponseDto binary = new UploadPresignedUrlResponseDto(binaryUrl, binaryPath);

        return new AdsUploadPresignedUrlResponseDto(original, binary);
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
     * 지정된 버전과 파일 이름을 가진 펌웨어의 Presigned 다운로드 URL을 생성하여 반환합니다.
     *
     * @param version  다운로드할 펌웨어의 버전 (예: "v1.0.0")
     * @param fileName 다운로드할 펌웨어의 파일 이름 (예: "firmware.bin")
     * @return S3 Presigned 다운로드 URL을 담은 {@link DownloadPresignedUrlResponseDto}
     */
    @Transactional
    public DownloadPresignedUrlResponseDto getPresignedDownloadUrl(String version, String fileName) {
        Optional<FirmwareMetadata> findMetadata = firmwareMetadataJpaRepository.findByVersionAndFileName(version, fileName);

        if (findMetadata.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 펌웨어 정보를 찾을 수 없습니다.");
        }

        GeneratePresignedUrlRequest generatedPresignedUrlRequest = generatePresignedDownloadUrl(bucketName, findMetadata.get().getS3Path());
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

    private String createOriginalFilePath(String title) {
        String uuid = UUID.randomUUID().toString();

        return String.format("ads/origin/%s/%s", uuid, title);
    }

    private String createBinaryFilePath(String title) {
        String uuid = UUID.randomUUID().toString();

        return String.format("ads/binary/%s/%s", uuid, title);
    }

    /**
     * 지정된 S3 객체의 SHA-256 해시값과 파일 크기를 계산합니다.
     *
     * @param path S3 버킷 내 객체의 경로 (예: "folder/file.txt")
     * @return {@link S3FileHashResult} 객체 (파일 크기와 SHA-256 해시값 포함)
     * @throws ResponseStatusException S3 접근 실패 또는 해시 계산 실패 시 발생
     */
    public S3FileHashResult calculateS3FileHash(String path) {

        try (S3Object s3Object = amazonS3.getObject(bucketName, path);
             InputStream inputStream = s3Object.getObjectContent()) {

            long fileSize = s3Object.getObjectMetadata().getContentLength();

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }

            byte[] hashBytes = digest.digest();

            return new S3FileHashResult(fileSize, bytesToHex(hashBytes));
        } catch (AmazonS3Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "S3 접근 오류: " + e.getMessage());
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "해시 계산 실패");
        }
    }

    /**
     * 바이트 배열을 16진수 문자열로 변환합니다.
     * 각 바이트를 2자리 16진수로 포맷하여 문자열로 이어붙인 결과를 반환합니다.
     *
     * @param bytes 변환할 바이트 배열
     * @return 16진수 문자열
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }
}
