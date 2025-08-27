package com.coffee_is_essential.iot_cloud_ota.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

/**
 * QuestDB 데이터베이스 설정 클래스입니다.
 * questdb.datasource.* 설정을 읽어와 HikariCP 기반의 DataSource를 생성하고,
 * 이를 활용한 {@link JdbcTemplate}, {@link NamedParameterJdbcTemplate}를 Bean으로 등록합니다.
 * QuestDB는 주로 시계열 데이터 저장 및 조회에 사용되며,
 * JPA가 아닌 JDBC 접근 방식을 통해 활용합니다.
 */
@Configuration
public class QuestDbConfig {
    @Bean(name = "questDbDataSource")
    @ConfigurationProperties(prefix = "questdb.datasource")
    public DataSource questDbDataSource() {
        return DataSourceBuilder.create()
                .type(com.zaxxer.hikari.HikariDataSource.class)
                .build();
    }

    @Bean(name = "questDbJdbcTemplate")
    public JdbcTemplate questDbJdbcTemplate(@Qualifier("questDbDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }

    @Bean(name = "questDbNamedJdbc")
    public NamedParameterJdbcTemplate questDbNamedJdbc(@Qualifier("questDbDataSource") DataSource ds) {
        return new NamedParameterJdbcTemplate(ds);
    }
}
