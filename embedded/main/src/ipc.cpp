#include "coffee/ipc.hpp"

namespace coffee {
	/**
	 * @brief  wifiTextArea에 표시될 텍스트를 전달하는 큐
	 * 
	 * 	       queue for delivering text to be displayed in wifiTextArea
	 */
	QueueHandle_t wifiTextArea_q;

	/**
	 * @brief  debugTextArea에 표시될 텍스트를 전달하는 큐
	 * 
	 * 	       queue for delivering text to be displayed in debugTextArea
	 */
	QueueHandle_t debugTextArea_q;

	bool init_ipc_queue(void) {
        wifiTextArea_q = xQueueCreate(COFFEE_QUEUE_SIZE, COFFEE_MAX_STR_BUF);
        debugTextArea_q = xQueueCreate(COFFEE_QUEUE_SIZE * 10, COFFEE_MAX_STR_BUF);

		if (!wifiTextArea_q || !debugTextArea_q) {
			Serial.println("ipc: queue creation failed");

			return false;
		}

		return true;
	}

    void send_message(QueueHandle_t& queue, const char* text) {
        char buf[COFFEE_MAX_STR_BUF];

        strncpy(buf, text, COFFEE_MAX_STR_BUF);
        buf[COFFEE_MAX_STR_BUF - 1] = '\0';

        xQueueSend(queue, buf, portMAX_DELAY);
    }

	bool receive_message(QueueHandle_t& queue, char* msg_out) {
		return (xQueueReceive(queue, msg_out, 0) == pdPASS);
	}
}
