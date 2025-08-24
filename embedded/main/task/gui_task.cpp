#include "gui_task.hpp"

namespace coffee {
    /**
     * @brief GUI 생성 및 업데이트 태스크
     * 
     *        task for creating and updating the GUI
     */
    static void gui_task(void* task_param);

    void init_gui(void) {
        ui_init();

        xTaskCreatePinnedToCore(coffee::gui_task, "gui", 8192, NULL, 4, NULL, 1);
    }

    static void gui_task(void* task_param) {
        while (true) {
            lv_timer_handler();

            delay(5);
        }

        vTaskDelete(NULL);
    }
}
