package com.coffee_is_essential.iot_cloud_ota.service;

import com.coffee_is_essential.iot_cloud_ota.repository.AdsDeploymentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeploymentAdsService {
    private final AdsDeploymentJpaRepository adsDeploymentJpaRepository;

    
}
