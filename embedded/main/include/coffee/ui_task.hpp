#ifndef COFFEE_UI_TASK_HPP
#define COFFEE_UI_TASK_HPP

#include <freertos/FreeRTOS.h>
#include <freertos/task.h>

#include <Arduino.h>

#include <ui.h>

#include "coffee/config.hpp"

namespace coffee {
    /**
     * @brief UI를 초기화하고 업데이트 태스크를 시작합니다
     * 
     *        initializes the UI and starts the update task
     */
    void init_ui_task(void);
}
#endif
