package com.coffee_is_essential.iot_cloud_ota.repository;

import com.coffee_is_essential.iot_cloud_ota.entity.FirmwareDeployment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

public interface FirmwareDeploymentRepository extends JpaRepository<FirmwareDeployment, Long> {
    Page<FirmwareDeployment> findAll(Pageable pageable);

    default FirmwareDeployment findByIdOrElseThrow(Long id) {
        return findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "[ID: " + id + "] 배포 정보를 찾을 수 없습니다."));
    }

    Optional<FirmwareDeployment> findByCommandId(String commandId);

    default FirmwareDeployment findByCommandIdOrElseThrow(String commandId) {
        return findByCommandId(commandId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "[commandId: " + commandId + "] 배포 정보를 찾을 수 없습니다."));
    }

    Page<FirmwareDeployment> findAllByFirmwareMetadataIsNotNull(Pageable pageable);
}
