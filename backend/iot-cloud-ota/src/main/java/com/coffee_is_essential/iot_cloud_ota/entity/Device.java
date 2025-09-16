package com.coffee_is_essential.iot_cloud_ota.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Device 엔티티는 하나의 디바이스 정보를 나타냅니다.
 * 각 디바이스는 이름, division, region을 가집니다.
 */
@Entity
@Table(name = "device")
@NoArgsConstructor
@AllArgsConstructor
public class Device extends BaseEntity {
    @Id
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "division_id")
    private Division division;

    @ManyToOne
    @JoinColumn(name = "region_id")
    private Region region;

    public Device(String name, Division division, Region region) {
        this.name = name;
        this.division = division;
        this.region = region;
    }

    public Long getDeviceId() {
        return id;
    }

    public Long getDivisionId() {
        return division.getId();
    }

    public Long getRegionId() {
        return region.getId();
    }

    public String getName() {
        return name;
    }

    public Division getDivision() {
        return division;
    }

    public Region getRegion() {
        return region;
    }
}
