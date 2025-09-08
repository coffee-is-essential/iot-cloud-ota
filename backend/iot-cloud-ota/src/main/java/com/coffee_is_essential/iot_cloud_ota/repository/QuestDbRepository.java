package com.coffee_is_essential.iot_cloud_ota.repository;

import com.coffee_is_essential.iot_cloud_ota.entity.FirmwareDownloadEvents;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class QuestDbRepository {
    private final @Qualifier("questDbJdbcTemplate") JdbcTemplate jdbcTemplate;
    private final @Qualifier("questDbNamedJdbc") NamedParameterJdbcTemplate namedJdbc;

    /**
     * 주어진 commandId에 해당하는 다운로드 이벤트 중
     * 디바이스별 최신 이벤트를 조회합니다.
     *
     * @param commandId 조회할 대상 command_id
     * @return 디바이스별 최신 펌웨어 다운로드 이벤트 리스트
     */
    public List<FirmwareDownloadEvents> findLatestPerDeviceByCommandId(String commandId) {
        String sql = """
                SELECT *
                FROM (
                    SELECT f.*,
                           ROW_NUMBER() OVER (PARTITION BY device_id ORDER BY "timestamp" DESC) AS rn
                    FROM download_events f
                    WHERE command_id = ?
                ) t
                WHERE rn = 1
                ORDER BY device_id
                """;
        return jdbcTemplate.query(sql, new Object[]{commandId}, mapper);
    }

    /**
     * 주어진 commandId와 디바이스 ID 목록에 해당하는 다운로드 이벤트 중
     * 디바이스별 최신 이벤트를 조회합니다.
     *
     * @param commandId 조회할 대상 command_id
     * @param deviceIds 조회할 대상 디바이스 ID 목록
     * @return 디바이스별 최신 펌웨어 다운로드 이벤트 리스트
     */
    public List<FirmwareDownloadEvents> findLatestPerDeviceByCommandIdAndDeviceIds(
            String commandId, List<Long> deviceIds) {
        if (deviceIds == null || deviceIds.isEmpty()) return List.of();

        String sql = """
                SELECT *
                FROM (
                    SELECT f.*,
                           ROW_NUMBER() OVER (PARTITION BY device_id ORDER BY "timestamp" DESC) rn
                    FROM download_events f
                    WHERE command_id = :cmd
                      AND device_id IN (:ids)
                ) t
                WHERE rn = 1
                ORDER BY device_id
                """;

        return namedJdbc.query(sql, Map.of("cmd", commandId, "ids", deviceIds), mapper);
    }

    /**
     * DownloadEvents 엔티티로 결과를 매핑하는 RowMapper 구현체입니다.
     */
    private final RowMapper<FirmwareDownloadEvents> mapper = new RowMapper<>() {
        @Override
        public FirmwareDownloadEvents mapRow(ResultSet rs, int rowNum) throws SQLException {
            FirmwareDownloadEvents e = new FirmwareDownloadEvents();
            e.setCommand_id(rs.getString("command_id"));
            e.setMessage(rs.getString("message"));
            e.setStatus(rs.getString("status"));
            e.setDeviceId(rs.getLong("device_id"));
            e.setProgress(rs.getLong("progress"));
            e.setTotalBytes(rs.getLong("total_bytes"));
            e.setDownloadBytes(rs.getLong("download_bytes"));
            e.setSpeedKbps(rs.getDouble("speed_kbps"));
            e.setChecksumVerified(rs.getBoolean("checksum_verified"));
            e.setDownloadTime(rs.getLong("download_seconds"));
            e.setTimestamp(rs.getTimestamp("timestamp"));

            return e;
        }
    };

    /**
     * 특정 디바이스에 대해 타임아웃 이벤트를 저장합니다.
     *
     * @param commandId 타임아웃이 발생한 command_id
     * @param deviceId  타임아웃이 발생한 디바이스 ID
     */
    public void saveTimeoutDevice(String commandId, Long deviceId) {
        jdbcTemplate.update(
                "INSERT INTO download_events (command_id, message, status, device_id, timestamp) VALUES (?, ?, ?, ?, NOW())",
                commandId, "Timeout", "TIMEOUT", deviceId
        );
    }
}
