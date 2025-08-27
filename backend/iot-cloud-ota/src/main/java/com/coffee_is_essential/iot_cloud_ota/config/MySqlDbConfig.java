package com.coffee_is_essential.iot_cloud_ota.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * MySQL 데이터베이스 설정 클래스입니다.
 * Spring Boot의 spring.datasource.* 설정을 읽어와
 * HikariCP 기반의 DataSource Bean을 생성합니다.
 * 이 Bean은 JPA/Hibernate가 사용할 기본 DataSource로 등록됩니다.
 */
@Configuration
public class MySqlDbConfig {
    @Primary
    @Bean(name = "mySqlDbDataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource mysqlDbDataSource() {
        return DataSourceBuilder.create()
                .type(com.zaxxer.hikari.HikariDataSource.class)
                .build();
    }
}
