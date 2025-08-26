#ifndef COFFEE_DEBUG_TASK_HPP
#define COFFEE_DEBUG_TASK_HPP

#include <cstddef>
#include <cstdint>
#include <string>

#include <esp_heap_caps.h>

#include <freertos/FreeRTOS.h>
#include <freertos/task.h>

#include <Arduino.h>
#include <HTTPClient.h>
#include <WiFi.h>
#include <WiFiClient.h>
#include <WiFiClientSecure.h>

#include <cJSON.h>

#include "coffee/config.hpp"
#include "coffee/ipc.hpp"

namespace coffee {
    /**
     * @brief 펌웨어 버전마다 달라지는 디버그용 함수 1
     * 
     *        debugging function 1 that varies depending on the firmware version
     * 
     * @param debug_param 디버그에 전달할 매개 변수
     * 
     *                    parameter passed for debugging
     */
    void debug1(void* debug_param);
}
#endif
