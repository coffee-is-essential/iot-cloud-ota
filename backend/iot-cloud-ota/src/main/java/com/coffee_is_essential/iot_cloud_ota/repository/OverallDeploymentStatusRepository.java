package com.coffee_is_essential.iot_cloud_ota.repository;

import com.coffee_is_essential.iot_cloud_ota.entity.OverallDeploymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

public interface OverallDeploymentStatusRepository extends JpaRepository<OverallDeploymentStatus, Long> {
    @Query(value = """
            select *
            from overall_deployment_status
            where deployment_id = :deploymentId
            order by created_at desc
            limit 1
            """, nativeQuery = true)
    Optional<OverallDeploymentStatus> findLatestByDeploymentId(@Param("deploymentId") Long id);

    default OverallDeploymentStatus findLatestByDeploymentIdOrElseThrow(Long id) {
        return findLatestByDeploymentId(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "[ID: " + id + "] 배포 정보를 찾을 수 없습니다."));
    }
}
