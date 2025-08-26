package com.coffee_is_essential.iot_cloud_ota.entity;

import com.coffee_is_essential.iot_cloud_ota.enums.DeploymentType;
import com.coffee_is_essential.iot_cloud_ota.enums.OverallDeploymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "firmware_deployment")
public class FirmwareDeployment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @ManyToOne(fetch = FetchType.LAZY)
    @ManyToOne
    @JoinColumn(name = "firmware_id")
    private FirmwareMetadata firmwareMetadata;

    @Column(nullable = false)
    private String commandId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeploymentType deploymentType;

    @Column(nullable = false)
    private LocalDateTime deployedAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OverallDeploymentStatus overallDeploymentStatus;

    public FirmwareDeployment(FirmwareMetadata firmwareMetadata, String commandId, DeploymentType deploymentType, LocalDateTime deployedAt, LocalDateTime expiresAt, OverallDeploymentStatus overallDeploymentStatus) {
        this.firmwareMetadata = firmwareMetadata;
        this.commandId = commandId;
        this.deploymentType = deploymentType;
        this.deployedAt = deployedAt;
        this.expiresAt = expiresAt;
        this.overallDeploymentStatus = overallDeploymentStatus;
    }
}
