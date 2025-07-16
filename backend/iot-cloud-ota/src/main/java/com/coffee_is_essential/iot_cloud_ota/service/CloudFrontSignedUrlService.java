package com.coffee_is_essential.iot_cloud_ota.service;

import com.amazonaws.services.cloudfront.CloudFrontUrlSigner;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import java.net.URL;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class CloudFrontSignedUrlService {

    private final SecretsManagerClient secretsManagerClient;

    @Value("${cloudfront.key.id}")
    private String keyPairId;

    @Value("${cloudfront.secret}")
    private String secretId;

    @Value("${cloudfront.domain}")
    private String cloudFrontDomain;

    public String generateSignedUrl(String resourcePath, Duration duration) throws Exception {
        String privateKeyPem = getPrivateKeyPem(secretId);
        PrivateKey privateKey = loadPrivateKeyFromPem(privateKeyPem);

        URL resourceUrl = new URL(cloudFrontDomain + "/" + resourcePath);
        Date expiresAt = Date.from(Instant.now().plus(duration));

        return CloudFrontUrlSigner.getSignedURLWithCannedPolicy(
                String.valueOf(resourceUrl),
                keyPairId,
                privateKey,
                expiresAt
        );
    }


    private String getPrivateKeyPem(String secretName) {
        GetSecretValueResponse response = secretsManagerClient.getSecretValue(
                GetSecretValueRequest.builder().secretId(secretName).build());

        return response.secretString();
    }

    private PrivateKey loadPrivateKeyFromPem(String pem) throws Exception {
        String cleaned = pem.replace("-----BEGIN RSA PRIVATE KEY-----", "")
                .replace("-----END RSA PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(cleaned);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);

        return KeyFactory.getInstance("RSA").generatePrivate(keySpec);
    }
}
