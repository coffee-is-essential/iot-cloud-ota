package com.coffee_is_essential.iot_cloud_ota.entity;


import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "advertisement_metadata")
public class AdvertisementMetadata extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, unique = true)
    private String originalS3Path;

    @Column(nullable = false, unique = true)
    private String binaryS3Path;

    @Column(nullable = false)
    private String fileHash;

    @Column(nullable = false)
    private long fileSize;
}
