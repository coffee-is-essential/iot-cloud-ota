#include "coffee/network_task.hpp"

namespace coffee {
    /**
     * @brief FreeRTOS 태스크를 통해 Wi-Fi 연결을 시도합니다
     * 
     *        attempts to connect to Wi-Fi using a FreeRTOS task
     */
    static void init_wifi_task(void* task_param);

    void connect_wifi(const char* ssid, const char* pw) {
        con_info* task_args = reinterpret_cast<con_info*>(pvPortMalloc(sizeof(con_info)));
        if (!task_args) {
            Serial.println("network_task: failed to allocate task_args");

            return;
        }

        task_args->ssid = ssid;
        task_args->pw = pw;

        xTaskCreatePinnedToCore(init_wifi_task, "init_wifi", 4096, task_args, tskIDLE_PRIORITY + 3, nullptr, 0);
    }

    static void init_wifi_task(void* task_param) {
        con_info* p_ci = reinterpret_cast<con_info*>(task_param);
        con_info ci = *p_ci;
        vPortFree(task_param);

        send_message(wifiTextArea_q, "Connecting...\n");

        if (coffee_drv::init_wifi_sta(ci.ssid, ci.pw)) {
            send_message(wifiTextArea_q, "Connected! IP address=");
            send_message(wifiTextArea_q, WiFi.localIP().toString().c_str());
            send_message(wifiTextArea_q, "\n");

            cJSON_SetValuestring(cJSON_GetObjectItem(wifi_config, "last_ssid"), ci.ssid);
            cJSON_SetValuestring(cJSON_GetObjectItem(wifi_config, "last_password"), ci.pw);

            write_config();
        } else {
            send_message(wifiTextArea_q, "Connection failed(Time Out)\n");
        }

        vTaskDelete(NULL);
    }
}
