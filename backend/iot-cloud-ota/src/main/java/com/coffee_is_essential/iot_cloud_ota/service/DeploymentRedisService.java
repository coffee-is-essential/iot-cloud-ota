package com.coffee_is_essential.iot_cloud_ota.service;

import com.coffee_is_essential.iot_cloud_ota.domain.DeployTargetDeviceInfo;
import com.coffee_is_essential.iot_cloud_ota.entity.FirmwareDownloadEvents;
import com.coffee_is_essential.iot_cloud_ota.repository.DownloadEventsJdbcRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Redis에 배포 대상 디바이스 ID를 저장/삭제/조회하고,
 * 필요 시 QuestDB에 타임아웃 이벤트를 기록하는 서비스.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DeploymentRedisService {
    private final StringRedisTemplate srt;
    private final DownloadEventsJdbcRepository downloadEventsJdbcRepository;

    /**
     * 배포 대상 디바이스들을 Redis Set에 추가한다.
     *
     * @param commandId         배포 식별자
     * @param targetDeviceInfos 배포 대상 디바이스 정보 목록
     */
    public void addDevices(String commandId, List<DeployTargetDeviceInfo> targetDeviceInfos) {
        srt.opsForSet().add(
                commandId,
                targetDeviceInfos.stream()
                        .map(target -> String.valueOf(target.deviceId()))
                        .toArray(String[]::new)
        );
    }

    /**
     * 완료된 디바이스들을 Redis Set에서 제거한다.
     *
     * @param commandId       배포 식별자
     * @param completedEvents 완료된 이벤트 목록
     */
    public void deleteDevices(String commandId, List<FirmwareDownloadEvents> completedEvents) {
        List<String> removedIds = completedEvents.stream()
                .map(e -> String.valueOf(e.getDeviceId()))
                .toList();

        srt.opsForSet().remove(commandId, removedIds.toArray());
        log.info(
                "[DELETED] commandId={}, removedIds={}, removedCount={}",
                commandId,
                removedIds,
                removedIds.size()
        );
    }

    /**
     * Redis Set에서 현재 남아있는 모든 디바이스 ID를 조회한다.
     *
     * @param commandId 배포 식별자
     * @return 디바이스 ID 리스트
     */
    public List<Long> getAllDeviceIdsFromRedisById(String commandId) {
        List<Long> deviceIds = srt.opsForSet().members(commandId).stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());

        return deviceIds;
    }

    /**
     * QuestDB에 지정된 디바이스들을 타임아웃 이벤트로 저장한다.
     *
     * @param commandId 배포 식별자
     * @param deviceIds 타임아웃 처리할 디바이스 ID 목록
     */
    @Transactional
    public void saveTimeoutDevices(String commandId, List<Long> deviceIds) {
        for (Long deviceId : deviceIds) {
            downloadEventsJdbcRepository.saveTimeoutDevice(commandId, deviceId);
        }
    }
}
