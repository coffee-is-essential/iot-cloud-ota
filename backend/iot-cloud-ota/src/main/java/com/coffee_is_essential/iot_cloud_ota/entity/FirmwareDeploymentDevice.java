package com.coffee_is_essential.iot_cloud_ota.entity;

import com.coffee_is_essential.iot_cloud_ota.enums.DeploymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "firmware_deployment_device")
@Getter
@NoArgsConstructor
public class FirmwareDeploymentDevice extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id")
    private Device device;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deployment_id")
    private FirmwareDeployment firmwareDeployment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeploymentStatus deploymentStatus;

    public FirmwareDeploymentDevice(Device device, FirmwareDeployment firmwareDeployment, DeploymentStatus deploymentStatus) {
        this.device = device;
        this.firmwareDeployment = firmwareDeployment;
        this.deploymentStatus = deploymentStatus;
    }
}
