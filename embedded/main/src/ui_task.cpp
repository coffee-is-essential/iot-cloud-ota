#include "coffee/ui_task.hpp"

namespace coffee {
    /**
     * @brief UI 업데이트 태스크
     * 
     *        UI updating task
     */
    static void ui_task(void* task_param);

    void init_ui_task(void) {
        ui_init();

        wifiTextArea_q = xQueueCreate(COFFEE_MAX_QUEUE, COFFEE_MAX_STR_BUF);

        xTaskCreatePinnedToCore(coffee::ui_task, "ui", 8192, NULL, 4, NULL, 1);
    }

    static void ui_task(void* task_param) {
        while (true) {
            lv_timer_handler();

            delay(5);
        }

        vTaskDelete(NULL);
    }
}
