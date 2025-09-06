package com.coffee_is_essential.iot_cloud_ota.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * DeviceAds 엔티티는 특정 디바이스에 할당된 광고 메타데이터를 나타냅니다.
 * 각 광고는 시작 시간과 종료 시간을 가지며, 현재 활성 상태를 나타내는 플래그도 포함됩니다.
 * 이 엔티티는 디바이스와 광고 메타데이터 간의 다대일 관계를 맺고 있습니다.
 */
@Entity
@Getter
@NoArgsConstructor
@Table(name = "device_ads")
public class DeviceAds {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @ManyToOne
    @JoinColumn(name = "ads_id", nullable = false)
    private AdsMetadata adsMetadata;

    @Column(name = "started_at", columnDefinition = "TIMESTAMP", nullable = false)
    private OffsetDateTime startedAt;

    @Column(name = "ended_at", columnDefinition = "TIMESTAMP")
    private OffsetDateTime endedAt;
}
