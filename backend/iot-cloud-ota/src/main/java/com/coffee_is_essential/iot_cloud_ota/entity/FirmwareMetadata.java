package com.coffee_is_essential.iot_cloud_ota.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 펌웨어 메타데이터 정보를 저장하는 엔티티 클래스입니다.
 * 버전, 파일명, 릴리즈 노트의 정보를 저장합니다.
 */
@Getter
@Entity
@Table(name = "firmware_metadata")
@NoArgsConstructor
public class FirmwareMetadata extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String version;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String releaseNote;

    @Column(nullable = false)
    private String s3Path;
    
    public FirmwareMetadata(String version, String fileName, String releaseNote, String s3Path) {
        this.version = version;
        this.fileName = fileName;
        this.releaseNote = releaseNote;
        this.s3Path = s3Path;
    }
}
