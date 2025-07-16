package com.coffee_is_essential.iot_cloud_ota.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

/**
 * AWS Secrets Manager 클라이언트를 설정하는 Spring Configuration 클래스 입니다.
 */
@Configuration
public class AwsSecretsManagerConfig {

    /**
     * {@link SecretsManagerClient} Bean을 생성합니다.
     *
     * @return SecretsManagerClient 인스턴스
     */
    @Bean
    public SecretsManagerClient secretsManagerClient() {
        return SecretsManagerClient.builder()
                .region(Region.AP_NORTHEAST_2)
                .build();
    }
}
