package com.coffee_is_essential.iot_cloud_ota.service;

import com.amazonaws.services.cloudfront.CloudFrontUrlSigner;
import com.coffee_is_essential.iot_cloud_ota.dto.DownloadSignedUrlResponseDto;
import com.coffee_is_essential.iot_cloud_ota.entity.AdsMetadata;
import com.coffee_is_essential.iot_cloud_ota.repository.AdsMetadataJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

/**
 * CloudFront Signed URL을 생성하는 서비스 클래스 입니다.
 * AWS Secrets Manager에서 비밀키(PEM)을 불러와, 주어진 경로에 대해 CloudFront SignedURL을 생성합니다.
 * 생성된 URL은 지정된 시간 동안만 유효합니다.
 */
@Service
@RequiredArgsConstructor
public class CloudFrontSignedUrlService {
    private final static int TIMEOUT = 10;

    private final SecretsManagerClient secretsManagerClient;
    private final AdsMetadataJpaRepository adsMetadataJpaRepository;

    @Value("${cloudfront.key.id}")
    private String keyPairId;

    @Value("${cloudfront.secret}")
    private String secretId;

    @Value("${cloudfront.domain}")
    private String cloudFrontDomain;

    /**
     * 주어진 리소스 경로에 대해 지정된 유효 시간 동안 사용할 . 있는 CloudFront Signed URL을 생성합니다.
     *
     * @param resourcePath CloudFront에서 접근할 리소스의 경로
     * @param expiresAt    URL이 유효한 시간
     * @return 서명된 CloudFront URL 문자열
     */
    public String generateSignedUrl(String resourcePath, Date expiresAt) {
        try {
            String privateKeyPem = getPrivateKeyPem(secretId);
            PrivateKey privateKey = loadPrivateKeyFromPem(privateKeyPem);
            String urlString = "https://" + cloudFrontDomain + "/" + resourcePath;
            return CloudFrontUrlSigner.getSignedURLWithCannedPolicy(
                    urlString,
                    keyPairId,
                    privateKey,
                    expiresAt
            );
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "CloudFront 서명 URL 생성 실패", e);
        }
    }

    /**
     * AWS Secrets Manager에서 PEM 형식의 비밀키를 조회합니다.
     *
     * @param secretName 시크릿의 이름 또는 ID
     * @return PEM 형식의 개인키 문자열
     */
    private String getPrivateKeyPem(String secretName) {
        GetSecretValueResponse response = secretsManagerClient.getSecretValue(
                GetSecretValueRequest.builder().secretId(secretName).build());

        return response.secretString();
    }

    /**
     * PEM 형식의 문자열에서 RSA 개인키 객체를 생성합니다.
     *
     * @param pem PEM 형식의 RSA 개인키 문자열
     * @return {@link PrivateKey} 객체
     * @throws Exception 키 파싱 중 오류 발생 시
     */
    private PrivateKey loadPrivateKeyFromPem(String pem) throws Exception {
        String cleaned = pem.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(cleaned);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);

        return KeyFactory.getInstance("RSA").generatePrivate(keySpec);
    }


    /**
     * 광고 제목에 해당하는 광고의 원본 S3 경로에 대해 CloudFront Signed URL을 생성합니다.
     * URL은 현재 시간으로부터 10분 동안 유효합니다.
     *
     * @param title 광고 제목
     * @return 서명된 CloudFront URL을 담은 응답 DTO
     * @throws ResponseStatusException 광고 제목이 존재하지 않거나 URL 생성 실패 시
     */
    public DownloadSignedUrlResponseDto generateAdsSignedUrl(String title) {
        Date expiresAt = Date.from(Instant.now().plus(Duration.ofMinutes(TIMEOUT)));
        AdsMetadata ads = adsMetadataJpaRepository.findByTitleOrElseThrow(title);
        String signedUrl = generateSignedUrl(ads.getOriginalS3Path(), expiresAt);

        return new DownloadSignedUrlResponseDto(signedUrl);
    }
}
