//package com.coffee_is_essential.iot_cloud_ota.component;
//
//import com.coffee_is_essential.iot_cloud_ota.domain.DeployTargetDeviceInfo;
//import com.coffee_is_essential.iot_cloud_ota.service.DeploymentRedisService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//public class RedisTest implements CommandLineRunner {
//    private final DeploymentRedisService redisService;
//
//    @Override
//    public void run(String... args) {
//        insert1();
//        insert2();
//    }
//
//    void insert1() {
//        List<DeployTargetDeviceInfo> list = List.of(new DeployTargetDeviceInfo(1L), new DeployTargetDeviceInfo(2L), new DeployTargetDeviceInfo(3L));
//        String commandId = "74be85bf-a800-4c7c-b591-d99e16b2dbaf";
//        redisService.addDevices(commandId, list);
//    }
//
//    void insert2() {
//        List<DeployTargetDeviceInfo> list = List.of(new DeployTargetDeviceInfo(2L), new DeployTargetDeviceInfo(3L));
//        String commandId = "3de487d0-5841-4001-a36f-2a0a9b0b50a5";
//        redisService.addDevices(commandId, list);
//    }
//}
