package com.coffee_is_essential.iot_cloud_ota.repository;

import com.coffee_is_essential.iot_cloud_ota.entity.FirmwareDeployment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public interface FirmwareDeploymentRepository extends JpaRepository<FirmwareDeployment, Long> {
    Page<FirmwareDeployment> findAll(Pageable pageable);

    default FirmwareDeployment findByIdOrElseThrow(Long id) {
        return findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "[ID: " + id + "] 배포 정보를 찾을 수 없습니다."));
    }
}
