package com.coffee_is_essential.iot_cloud_ota.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Divison 엔티티는 디바이스의 그룹을 나타냅니다.
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "division")
public class Division {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "division_code", nullable = false, unique = true)
    private String divisionCode;

    @Column(name = "division_name", nullable = false)
    private String divisionName;

    public Division(String divisionCode, String divisionName) {
        this.divisionCode = divisionCode;
        this.divisionName = divisionName;
    }
}
