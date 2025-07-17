package com.coffee_is_essential.iot_cloud_ota.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA Auditing을 활성화하기 위한 설정 클래스입니다.
 * 이 설정을 통해 엔티티 클래스의 CreatedDate, ModifiedDate}와 같은 Auditing 필드를 자동으로 관리할 수 있습니다.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
