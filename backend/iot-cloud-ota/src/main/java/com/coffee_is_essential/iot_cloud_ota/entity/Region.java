package com.coffee_is_essential.iot_cloud_ota.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 리전 정보를 데이터베이스에 저장하기 위한 엔티티 클래스 입니다.
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@org.hibernate.annotations.Immutable
@Table(name = "region")
public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "region_code", nullable = false, unique = true)
    private String regionCode;

    @Column(name = "region_name", nullable = false)
    private String regionName;

    public Region(String regionCode, String regionName) {
        this.regionCode = regionCode;
        this.regionName = regionName;
    }
}
