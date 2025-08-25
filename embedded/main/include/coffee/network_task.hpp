#ifndef COFFEE_NETWORK_TASK_HPP
#define COFFEE_NETWORK_TASK_HPP

#include <freertos/FreeRTOS.h>
#include <freertos/task.h>
#include <freertos/queue.h>

#include <coffee_drv/wifi.hpp>

#include "coffee/config.hpp"

namespace coffee {
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

    /**
     * @brief 마지막 Wi-Fi 연결 정보를 이용하여 Wi-Fi에 연결합니다
     * 
     *        connects to Wi-Fi using the last saved connection information
     */
    void restore_wifi(void);

    extern QueueHandle_t wifiTextArea_q;
}
#endif
