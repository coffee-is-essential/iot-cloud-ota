#ifndef COFFEE_IPC_HPP
#define COFFEE_IPC_HPP

#include <freertos/FreeRTOS.h>
#include <freertos/queue.h>

#include <Arduino.h>

#include "coffee/config.hpp"

namespace coffee {
    /**
     * @brief IPC를 위한 큐를 초기화합니다
     * 
     *        initializes the queue for IPC
     * 
     * @return 초기화 성공 여부
     * 
     *         whether initialization succeeded
     */
    bool init_ipc_queue(void);

    /**
     * @brief 큐를 이용하여 메시지를 송신합니다
     * 
     *        sends a message using the queue
     * 
     * @param queue 메시지를 송신할 큐
     * 
     *              the queue to which the message will be sent
     * 
     * @param text 송신 텍스트
     * 
     *             the text message to send
     */
    void send_message(QueueHandle_t& queue, const char* text);

    /**
     * @brief 큐를 이용하여 메시지를 수신합니다
     * 
     *        receives a message using the queue
     * 
     * @param queue 메시지를 수신할 큐
     * 
     *              the queue from which the message will be received
     * 
     * @param[out] msg_out 수신 메시지 버퍼
     * 
     *                  the destination buffer for the received message
     * 
     * @return 메시지 수신 여부
     * 
     *         whether the message was successfully received
     */
    bool receive_message(QueueHandle_t& queue, char* msg_out);

    extern QueueHandle_t wifiTextArea_q;

    extern QueueHandle_t debugTextArea_q;
}
#endif
