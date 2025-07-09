package com.coffee_is_essential.iot_cloud_ota.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/**
 * AWS 리전 정보를 데이터베이스에 저장하기 위한 엔티티 클래스 입니다.
 */
@Entity
public class Region {
    @Id
    private Long id;

    @Column(name = "region_id", nullable = false, unique = true)
    private String regionId;

    @Column(name = "region_name", nullable = false)
    private String regionName;
}
