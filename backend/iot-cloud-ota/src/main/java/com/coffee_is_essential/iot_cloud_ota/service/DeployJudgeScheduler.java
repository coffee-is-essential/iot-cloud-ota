package com.coffee_is_essential.iot_cloud_ota.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DeployJudgeScheduler {
    private final StringRedisTemplate srt;

    // 1분마다 실행
    @Scheduled(fixedDelay = 60_000)
    public void dumpDeploySets() {
        System.out.println("=== Redis Dump Start ===");
        var conn = Objects.requireNonNull(srt.getConnectionFactory()).getConnection();

        try (Cursor<byte[]> cursor = conn.scan(
                ScanOptions.scanOptions().match("ota:deploy:devices:*").count(200).build())) {

            while (cursor.hasNext()) {
                String key = new String(cursor.next(), StandardCharsets.UTF_8);
                // 키는 전부 Set이므로 안전하게 members()
                Set<String> members = srt.opsForSet().members(key);
                System.out.printf("[SET] %s = %s%n", key, members);
            }
        } catch (Exception e) {
            System.out.println("[SCAN ERROR] " + e.getMessage());
        } finally {
            conn.close();
        }
        System.out.println("=== Redis Dump End ===");
    }
}
