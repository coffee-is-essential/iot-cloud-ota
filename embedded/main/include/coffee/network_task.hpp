#ifndef COFFEE_NETWORK_TASK_HPP
#define COFFEE_NETWORK_TASK_HPP

#include <freertos/FreeRTOS.h>
#include <freertos/task.h>
#include <freertos/queue.h>

#include <cJSON.h>

#include <coffee_drv/wifi.hpp>

#include "coffee/config.hpp"
#include "coffee/ipc.hpp"

namespace coffee {
    typedef struct con_info {
        const char* ssid;
        const char* pw;
    } con_info;

    /**
     * @brief Wi-Fi에 연결합니다
     * 
     *        connects to Wi-Fi
     *
     * @param ssid 연결할 Wi-Fi의 SSID
     * 
     *             the SSID of the Wi-Fi to connect
     * 
     * @param pw 연결할 Wi-Fi의 비밀번호
     * 
     *           the password of the Wi-Fi to connect
     */
    void connect_wifi(const char* ssid, const char* pw);

    extern QueueHandle_t wifiTextArea_q;

    extern QueueHandle_t debugTextArea_q;
}
#endif
