package com.coffee_is_essential.iot_cloud_ota.repository;

import com.coffee_is_essential.iot_cloud_ota.entity.Division;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public interface DivisionJpaRepository extends JpaRepository<Division, Long> {
    /**
     * 주어진 ID로 디비전을 조회하고, 존재하지 않을 경우 {@link ResponseStatusException} 예외를 발생시킵니다.
     *
     * @param id 조회할 디비전의 ID
     * @return 존재하는 디비전 엔티티
     */
    default Division findByIdOrElseThrow(Long id) {
        return findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "[ID: " + id + "] 그룹을 찾을 수 없습니다."));
    }
}
