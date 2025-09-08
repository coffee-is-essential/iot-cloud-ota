package com.coffee_is_essential.iot_cloud_ota.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "ads_deployment",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"deployment_id", "advertisement_id"})
        }
)
public class AdsDeployment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "deployment_id", nullable = false)
    FirmwareDeployment firmwareDeployment;

    @ManyToOne
    @JoinColumn(name = "advertisement_id", nullable = false)
    AdsMetadata adsMetadata;

    public AdsDeployment(FirmwareDeployment firmwareDeployment, AdsMetadata adsMetadata) {
        this.firmwareDeployment = firmwareDeployment;
        this.adsMetadata = adsMetadata;
    }
}
