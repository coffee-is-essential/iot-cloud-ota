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

        xTaskCreatePinnedToCore(coffee::ui_task, "ui", 8192, nullptr, tskIDLE_PRIORITY + 1, nullptr, 1);
    }

    static void ui_task(void* task_param) {
        while (true) {
            lv_timer_handler();

            delay(5);
        }

        vTaskDelete(nullptr);
    }
}
