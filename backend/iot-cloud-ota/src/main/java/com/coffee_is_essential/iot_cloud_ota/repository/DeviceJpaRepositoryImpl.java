package com.coffee_is_essential.iot_cloud_ota.repository;

import com.coffee_is_essential.iot_cloud_ota.domain.DeployTargetDeviceInfo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DeviceJpaRepositoryImpl implements DeviceJpaRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<DeployTargetDeviceInfo> findByFilterDynamic(List<Long> deviceIds, List<Long> groupIds, List<Long> regionIds) {
        StringBuilder jpql = new StringBuilder(
                "SELECT new com.coffee_is_essential.iot_cloud_ota.domain.DeployTargetDeviceInfo(d.id, d.division.id, d.region.id) " +
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

        TypedQuery<DeployTargetDeviceInfo> query = em.createQuery(jpql.toString(), DeployTargetDeviceInfo.class);
        if (!deviceIds.isEmpty()) query.setParameter("deviceIds", deviceIds);
        if (!groupIds.isEmpty()) query.setParameter("groupIds", groupIds);
        if (!regionIds.isEmpty()) query.setParameter("regionIds", regionIds);

        return query.getResultList();
    }
}
