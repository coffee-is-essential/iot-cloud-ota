package com.coffee_is_essential.iot_cloud_ota.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "ads_metadata")
public class AdsMetadata extends BaseEntity {
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

    public AdsMetadata(String title, String description, String originalS3Path, String binaryS3Path, String fileHash, long fileSize) {
        this.title = title;
        this.description = description;
        this.originalS3Path = originalS3Path;
        this.binaryS3Path = binaryS3Path;
        this.fileHash = fileHash;
        this.fileSize = fileSize;
    }
}
