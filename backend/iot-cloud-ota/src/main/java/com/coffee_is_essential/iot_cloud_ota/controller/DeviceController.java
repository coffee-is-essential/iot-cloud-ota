package com.coffee_is_essential.iot_cloud_ota.controller;

import com.coffee_is_essential.iot_cloud_ota.domain.PaginationInfo;
import com.coffee_is_essential.iot_cloud_ota.dto.DeviceListResponseDto;
import com.coffee_is_essential.iot_cloud_ota.dto.DeviceSummaryResponseDto;
import com.coffee_is_essential.iot_cloud_ota.service.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
