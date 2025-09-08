package com.coffee_is_essential.iot_cloud_ota.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "device_firmware")
public class DeviceFirmware {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @ManyToOne
    @JoinColumn(name = "firmware_id", nullable = false)
    private FirmwareMetadata firmware;

    @Column(name = "started_at", columnDefinition = "TIMESTAMP", nullable = false)
    private OffsetDateTime startedAt;

    @Column(name = "ended_at", columnDefinition = "TIMESTAMP")
    private OffsetDateTime endedAt;

    public DeviceFirmware(Device device, FirmwareMetadata firmware, OffsetDateTime startedAt) {
        this.device = device;
        this.firmware = firmware;
        this.startedAt = startedAt;
    }
}
