package com.coffee_is_essential.iot_cloud_ota.entity;

import com.coffee_is_essential.iot_cloud_ota.enums.DeploymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Table(name = "overall_deployment_status")
@Getter
public class OverallDeploymentStatus extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "deployment_id")
    FirmwareDeployment firmwareDeployment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    DeploymentStatus deploymentStatus;

    public OverallDeploymentStatus(FirmwareDeployment firmwareDeployment, DeploymentStatus deploymentStatus) {
        this.firmwareDeployment = firmwareDeployment;
        this.deploymentStatus = deploymentStatus;
    }
}
