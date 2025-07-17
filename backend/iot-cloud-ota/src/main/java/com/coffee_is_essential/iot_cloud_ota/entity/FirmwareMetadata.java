package com.coffee_is_essential.iot_cloud_ota.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 펌웨어 메타데이터 정보를 저장하는 엔티티 클래스입니다.
 * 버전, 파일명, 릴리즈 노트의 정보, 파일 크기, 체크섬을 저장합니다.
 */
@Getter
@Entity
@Table(
        name = "firmware_metadata",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_filename_version", columnNames = {"fileName", "version"})
        }
)
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

    @Column(nullable = false, unique = true)
    private String s3Path;

    @Column(nullable = false)
    private String fileHash;

    @Column(nullable = false)
    private long fileSize;

//    public FirmwareMetadata(String version, String fileName, String releaseNote, String s3Path) {
//        this.version = version;
//        this.fileName = fileName;
//        this.releaseNote = releaseNote;
//        this.s3Path = s3Path;
//    }

    public FirmwareMetadata(String version, String fileName, String releaseNote, String s3Path, String fileHash, long fileSize) {
        this.version = version;
        this.fileName = fileName;
        this.releaseNote = releaseNote;
        this.s3Path = s3Path;
        this.fileHash = fileHash;
        this.fileSize = fileSize;
    }
}
