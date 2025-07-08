package com.coffee_is_essential.iot_cloud_ota.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * S3Config 클래스는 AWS S3와의 연동을 위한 설정을 구성합니다.
 * AWS 자격 증명 및 AmazonS3 클라이언트를 Bean으로 등록합니다.
 */
@Configuration
public class S3Config {
    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    /**
     * AWS 자격 증명을 생성합니다.
     *
     * @return BasicAWSCredentials AWS 접근 키와 비밀 키를 포함하는 자격 증명 객체
     */
    @Bean
    @Primary
    public BasicAWSCredentials awsCredentialsProvider() {
        BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(accessKey, secretKey);

        return basicAWSCredentials;
    }

    /**
     * AmazonS3 클라이언트를 생성하여 S3와의 연동을 가능하게 합니다.
     *
     * @return AmazonS3 AWS S3와 통신할 수 있는 클라이언트
     */
    @Bean
    public AmazonS3 amazonS3() {
        AmazonS3 s3Builder = AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentialsProvider()))
                .build();

        return s3Builder;
    }
}
