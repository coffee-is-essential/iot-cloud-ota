package com.coffee_is_essential.iot_cloud_ota.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;

/**
 * 엔티티 생성 및 수정 시간을 자동으로 관리하는 기본 엔티티 클래스입니다.
 * 모든 엔티티에서 공통적으로 상속받아 사용합니다.
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {
    @CreationTimestamp
    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP")
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "modified_at", columnDefinition = "TIMESTAMP")
    private OffsetDateTime modifiedAt;

    protected BaseEntity() {

    }
}
