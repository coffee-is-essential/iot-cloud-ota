package com.coffee_is_essential.iot_cloud_ota.repository;

import com.coffee_is_essential.iot_cloud_ota.entity.SystemStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DeviceStatusJdbcRepository {
    private final @Qualifier("questDbJdbcTemplate") JdbcTemplate jdbcTemplate;
    private final @Qualifier("questDbNamedJdbc") NamedParameterJdbcTemplate namedJdbc;

    /**
     * 주어진 deviceId에 해당하는 시스템 상태 정보 중
     * 가장 최신의 상태 정보를 조회합니다.
     *
     * @param deviceId 조회할 대상 device_id
     * @return 가장 최신의 시스템 상태 정보
     */
    public SystemStatus findLatestByDeviceId(Long deviceId) {
        String sql = """
                SELECT *
                FROM system_status
                WHERE device_id = ?
                ORDER BY "timestamp" DESC
                LIMIT 1
                """;
        List<SystemStatus> results = jdbcTemplate.query(sql, mapper, deviceId);

        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * 주어진 deviceId 목록에 해당하는 시스템 상태 정보 중
     * 디바이스별 최신 상태 정보를 조회합니다.
     *
     * @param deviceIds 조회할 대상 device_id 목록
     * @return 디바이스별 최신 시스템 상태 정보 리스트
     */
    private final RowMapper<SystemStatus> mapper = new RowMapper<>() {
        @Override
        public SystemStatus mapRow(ResultSet rs, int rowNum) throws SQLException {
            SystemStatus status = new SystemStatus();
            status.setDeviceId(rs.getLong("device_id"));
            status.setCpuCore0(rs.getDouble("cpu_core_0"));
            status.setCpuCore1(rs.getDouble("cpu_core_1"));
            status.setMemoryUsage(rs.getDouble("memory_usage"));
            status.setStorageUsage(rs.getDouble("storage_usage"));
            status.setUptime(rs.getLong("uptime"));
            status.setTimestamp(rs.getObject("timestamp", OffsetDateTime.class));

            return status;
        }
    };
}
