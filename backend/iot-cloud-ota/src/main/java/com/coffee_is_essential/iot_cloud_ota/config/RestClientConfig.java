package com.coffee_is_essential.iot_cloud_ota.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;

@Configuration
public class RestClientConfig {
    private static final int CONNECTION_TIMEOUT = 5;
    private static final int READ_TIMEOUT = 5;

    @Bean
    public RestClient restClient(RestClient.Builder builder) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(CONNECTION_TIMEOUT));
        requestFactory.setReadTimeout(Duration.ofSeconds(READ_TIMEOUT));

        return RestClient.builder()
                .requestFactory(requestFactory)
                .defaultStatusHandler(
                        statusCode -> statusCode.is4xxClientError() || statusCode.is5xxServerError(),
                        (request, response) -> {
                            if (response.getStatusCode().is5xxServerError()) {
                                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "MQTT Handler 오류 발생");
                            }
                            if (response.getStatusCode().is4xxClientError()) {
                                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "요청이 잘못되었습니다.");
                            }
                        })
                .build();
    }
}
