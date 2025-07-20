package com.coffee_is_essential.iot_cloud_ota.config;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * S3Config 클래스는 AWS S3와의 연동을 위한 설정을 구성합니다.
 * AmazonS3 클라이언트를 Bean으로 등록합니다.
 */
@Configuration
public class S3Config {

    @Value("${cloud.aws.region.static}")
    private String region;

    /**
     * AmazonS3 클라이언트를 생성하여 S3와의 연동을 가능하게 합니다.
     * Default Credentials Provider Chain(IAM Role)을 사용하여 AWS 자격 증명을 자동으로 처리합니다.
     * AWS 공식 문서 참조: https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html#credentials-default
     *
     * @return AmazonS3 AWS S3와 통신할 수 있는 클라이언트
     */
    @Bean
    public AmazonS3 amazonS3() {
        return AmazonS3ClientBuilder.standard()
            .withRegion(region)
            .build();
    }
}
