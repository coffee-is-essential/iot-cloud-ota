package com.coffee_is_essential.iot_cloud_ota.repository;

import com.coffee_is_essential.iot_cloud_ota.entity.Device;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * DeviceJpaRepositoryCustom 인터페이스의 구현체로,
 * 동적으로 디바이스 필터링 조건을 구성하여 디바이스 정보를 조회합니다.
 */
@Repository
public class DeviceJpaRepositoryImpl implements DeviceJpaRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    /**
     * 전달된 deviceId, groupId, regionId 리스트 중 하나라도 일치하는 디바이스를 조회합니다.
     * 조건이 없을 경우 해당 조건은 무시됩니다.
     *
     * @param deviceIds 조회할 디바이스 ID 리스트
     * @param groupIds  조회할 그룹 ID 리스트
     * @param regionIds 조회할 리전 ID 리스트
     * @return 조건에 해당하는 디바이스들의 DTO 리스트
     */
    @Override
    public List<Device> findByFilterDynamic(List<Long> deviceIds, List<Long> groupIds, List<Long> regionIds) {
        StringBuilder jpql = new StringBuilder(
                "SELECT d " +
                "FROM Device d " +
                "WHERE 1=0"
        );

        if (!deviceIds.isEmpty()) {
            jpql.append(" OR d.id IN :deviceIds");
        }

        if (!groupIds.isEmpty()) {
            jpql.append(" OR d.division.id IN :groupIds");
        }

        if (!regionIds.isEmpty()) {
            jpql.append(" OR d.region.id IN :regionIds");
        }

        TypedQuery<Device> query = em.createQuery(jpql.toString(), Device.class);
        if (!deviceIds.isEmpty()) query.setParameter("deviceIds", deviceIds);
        if (!groupIds.isEmpty()) query.setParameter("groupIds", groupIds);
        if (!regionIds.isEmpty()) query.setParameter("regionIds", regionIds);

        return query.getResultList();
    }
}
