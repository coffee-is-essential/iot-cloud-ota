#ifndef COFFEE_UI_TASK_HPP
#define COFFEE_UI_TASK_HPP

#include <freertos/FreeRTOS.h>
#include <freertos/task.h>
#include <freertos/queue.h>

#include <Arduino.h>

#include <ui.h>

#include "coffee/config.hpp"

#define COFFEE_MAX_QUEUE 8

namespace coffee {
    /**
     * @brief UI를 초기화하고 업데이트 태스크를 시작합니다
     * 
     *        initializes the UI and starts the update task
     */
    void init_ui_task(void);

    extern QueueHandle_t wifiTextArea_q;
}
#endif
