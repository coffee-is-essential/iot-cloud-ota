package com.coffee_is_essential.iot_cloud_ota.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Region {
    @Id
    private Long id;

    @Column(name = "region_id", nullable = false, unique = true)
    private String regionId;

    @Column(name = "region_name", nullable = false)
    private String regionName;
}
