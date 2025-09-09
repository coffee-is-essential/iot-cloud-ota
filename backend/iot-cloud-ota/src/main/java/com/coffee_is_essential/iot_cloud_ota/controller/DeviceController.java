package com.coffee_is_essential.iot_cloud_ota.controller;

import com.coffee_is_essential.iot_cloud_ota.domain.PaginationInfo;
import com.coffee_is_essential.iot_cloud_ota.dto.DeviceDetailResponseDto;
import com.coffee_is_essential.iot_cloud_ota.dto.DeviceListResponseDto;
import com.coffee_is_essential.iot_cloud_ota.dto.DeviceSummaryResponseDto;
import com.coffee_is_essential.iot_cloud_ota.service.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Device 관련 요청을 처리하는 REST 컨트롤러 입니다.
 */
@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController {
    private final DeviceService deviceService;

    /**
     * 모든 디바이스의 요약 정보를 조회합니다.
     * 디바이스의 요약 정보에는 디바이스 아이디, 디바이스 이름, 리전 이름, 그룹 이름, 활성화 상태 여부를 포함합니다.
     *
     * @return 디바이스 요약 정보를 담은 DeviceSummaryResponseDto 리스트와 HTTP 200 응답
     */
    @GetMapping
    public ResponseEntity<List<DeviceSummaryResponseDto>> findDeviceSummary() {
        List<DeviceSummaryResponseDto> responseDtos = deviceService.findDeviceSummary();

        return new ResponseEntity<>(responseDtos, HttpStatus.OK);
    }

    /**
     * 디바이스 목록을 조회합니다.
     * 선택적으로 regionId와 groupId로 필터링할 수 있으며, 페이지네이션을 지원합니다.
     *
     * @param regionId (선택 사항) 필터링할 리전 ID
     * @param groupId  (선택 사항) 필터링할 그룹 ID
     * @param page     (기본값: 1) 조회할 페이지 번호
     * @param limit    (기본값: 10) 페이지당 조회할 디바이스 수
     * @return 필터링 및 페이지네이션된 디바이스 목록과 HTTP 200 응답
     */
    @GetMapping("/list")
    public ResponseEntity<DeviceListResponseDto> findAllDevices(
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) Long groupId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        PaginationInfo paginationInfo = PaginationInfo.of(page, limit);
        DeviceListResponseDto responseDto = deviceService.findAllDevices(regionId, groupId, paginationInfo);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    /**
     * 특정 디바이스의 상세 정보를 조회합니다.
     * 디바이스의 상세 정보에는 디바이스 아이디, 디바이스 이름, 리전 이름, 그룹 이름, 활성화 상태 여부,
     * 마지막 연결 시간, 펌웨어 버전, IP 주소, MAC 주소를 포함합니다.
     *
     * @param id 조회할 디바이스의 ID
     * @return 디바이스 상세 정보를 담은 DeviceDetailResponseDto와 HTTP 200 응답
     */
    @GetMapping("{id}")
    public ResponseEntity<DeviceDetailResponseDto> findDetailByDeviceId(@PathVariable Long id) {
        DeviceDetailResponseDto responseDto = deviceService.findDetailByDeviceId(id);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
