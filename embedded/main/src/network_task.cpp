#include "coffee/network_task.hpp"

namespace coffee {
    /**
     * @brief 텍스트를 큐에 추가합니다
     * 
     *        enqueues the given text into the queue
     */
    static inline void queue_text(QueueHandle_t* queue, const char* text);

    void connect_wifi(const char* ssid, const char* pw) {
        // 추가 예정
    }

    void restore_wifi(void) {
        // 큐에 텍스트 추가하는 로직 추가
        
        // 아래 로직 자체를 별도의 freertos 태스크로 빼기
        const char *last_ssid = nullptr, *last_password = nullptr;
        if (coffee::get_last_wifi(&last_ssid, &last_password)) {
            coffee_drv::init_wifi_sta(last_ssid, last_password);
        }
    }

    static inline void queue_text(QueueHandle_t* queue, const char* text) {
        char buf[COFFEE_MAX_STR_BUF];

        strncpy(buf, text, COFFEE_MAX_STR_BUF);
        buf[COFFEE_MAX_STR_BUF - 1] = '\0';

        xQueueSend(*queue, buf, portMAX_DELAY);
    }
}
