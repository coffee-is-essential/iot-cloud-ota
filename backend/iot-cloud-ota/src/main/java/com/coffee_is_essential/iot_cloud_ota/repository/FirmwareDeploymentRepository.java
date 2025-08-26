package com.coffee_is_essential.iot_cloud_ota.repository;

import com.coffee_is_essential.iot_cloud_ota.entity.FirmwareDeployment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FirmwareDeploymentRepository extends JpaRepository<FirmwareDeployment, Long> {
    Page<FirmwareDeployment> findAll(Pageable pageable);
}
