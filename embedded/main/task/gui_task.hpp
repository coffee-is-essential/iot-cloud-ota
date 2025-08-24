#ifndef COFFEE_GUI_TASK_HPP
#define COFFEE_GUI_TASK_HPP

#include <freertos/FreeRTOS.h>
#include <freertos/task.h>

#include <Arduino.h>

#include <ui.h>

namespace coffee {
    /**
     * @brief 동시 접근 제어 객체들을 초기화하고 GUI 태스크를 생성합니다
     * 
     *        initializes synchronization objects and creates the GUI task
     */
    void init_gui(void);
}
#endif
