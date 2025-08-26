#include "coffee/debug_task.hpp"

namespace coffee {
    /**
     * @brief debug1 함수를 위한 FreeRTOS 태스크
     * 
     *        FreeRTOS task for executing the debug1 function
     */
    static void debug1_task(void* task_param);

    void debug1(void* debug_param) {
        xTaskCreatePinnedToCore(debug1_task, "debug1", 4096, debug_param, tskIDLE_PRIORITY + 2, nullptr, 0);
    }

    static void debug1_task(void* task_param) {
        send_message(debugTextArea_q, "firmware download started\n");

        char* firmware_id = reinterpret_cast<char*>(task_param);
        send_message(debugTextArea_q, "firmware ID: ");
        send_message(debugTextArea_q, firmware_id);
        send_message(debugTextArea_q, "\n");

        HTTPClient http;
        http.setConnectTimeout(COFFEE_NETWORK_TIMEOUT_MS);
        http.setTimeout(COFFEE_NETWORK_TIMEOUT_MS);

        std::string endpoint = std::string("http://backend-alb-497054189.ap-northeast-2.elb.amazonaws.com/api/firmwares/metadata/") + firmware_id + "/deployment";

        if (!http.begin(endpoint.c_str())) {
            send_message(debugTextArea_q, "HTTP begin failed\n");

            vTaskDelete(nullptr);
        }

        http.addHeader("Content-Type", "application/json");

        std::string req_body(R"r(
            {
                "deviceIds": [1, 2, 3],
                "groupIds": [],
                "regionIds": []
            })r");

        int res_code = http.POST(String(req_body.c_str()));
        if (res_code != HTTP_CODE_OK) {
            send_message(debugTextArea_q, "HTTP POST failed, code=");
            send_message(debugTextArea_q, std::to_string(res_code).c_str());
            send_message(debugTextArea_q, "\n");

            http.end();
            
            vTaskDelete(nullptr);
        }

        std::string res_body(http.getString().c_str());

        http.end();

        cJSON* res_root = cJSON_Parse(res_body.c_str());
        if (!res_root) {
            send_message(debugTextArea_q, "Respond parse failed\n");

            vTaskDelete(nullptr);
        }

        std::string signed_url;
        cJSON* res_signed_url = cJSON_GetObjectItem(res_root, "signedUrl");
        if (!res_signed_url) {
            send_message(debugTextArea_q, "HTTP response does not contain a signedUrl\n");

            cJSON_Delete(res_root);

            vTaskDelete(nullptr);
        } else if (!cJSON_IsString(res_signed_url)) {
            send_message(debugTextArea_q, "Invalid signedUrl\n");

            cJSON_Delete(res_root);

            vTaskDelete(nullptr);
        } else {
            signed_url = std::string(res_signed_url->valuestring);
        }

        std::string version;
        cJSON* res_version = cJSON_GetObjectItem(res_root, "version");
        if (!res_version) {
            send_message(debugTextArea_q, "HTTP response does not contain a version\n");

            cJSON_Delete(res_root);

            vTaskDelete(nullptr);
        } else if (!cJSON_IsString(res_version)) {
            send_message(debugTextArea_q, "Invalid version\n");

            cJSON_Delete(res_root);

            vTaskDelete(nullptr);
        } else {
            version = std::string(res_version->valuestring);
        }

        send_message(debugTextArea_q, "firmware version: v");
        send_message(debugTextArea_q, version.c_str());
        send_message(debugTextArea_q, "\n");

        // 여기서부터 수정
        int file_size = cJSON_GetObjectItem(cJSON_GetObjectItem(res_root, "fileInfo"), "fileSize")->valueint;
        send_message(debugTextArea_q, "firmware size: ");
        send_message(debugTextArea_q, std::to_string(version).c_str());
        send_message(debugTextArea_q, "B\n");

        cJSON_Delete(res_root);

        WiFiClientSecure client;
        client.setTimeout(COFFEE_NETWORK_TIMEOUT_MS);
        client.setInsecure();

        if (!http.begin(client, signed_url.c_str())) {
            send_message(debugTextArea_q, "HTTPS begin failed\n");

            vTaskDelete(nullptr);
        }

        res_code = http.GET();
        if (res_code != HTTP_CODE_OK) {
            send_message(debugTextArea_q, "HTTPS GET failed, code=");
            send_message(debugTextArea_q, std::to_string(res_code).c_str());
            send_message(debugTextArea_q, "\n");

            http.end();

            vTaskDelete(nullptr);
        }

        File new_firmware = SD.open(std::string(std::string("/ota/v") + version + ".bin").c_str(), FILE_WRITE);
        if (!new_firmware) {
            send_message(debugTextArea_q, "file creation failed\n");

            http.end();

            vTaskDelete(nullptr);
        }

        WiFiClient* stream = http.getStreamPtr();
        
        uint8_t* buf = reinterpret_cast<uint8_t*>(heap_caps_malloc(COFFEE_FILE_CHUNK_SIZE, MALLOC_CAP_SPIRAM));
        if (!buf) {
            send_message(debugTextArea_q, "file buffer allocation failed\n");

            new_firmware.close();

            http.end();

            vTaskDelete(nullptr);
        }

        std::size_t total_downloaded = 0;
        std::size_t download_size = 0;
        while ((download_size = stream->readBytes(buf, COFFEE_FILE_CHUNK_SIZE)) > 0) {
            if (new_firmware.write(buf, download_size) != download_size) {
                send_message(debugTextArea_q, "firmware download failed\n");

                heap_caps_free(buf);

                new_firmware.close();
                
                http.end();

                vTaskDelete(nullptr);
            }

            total_downloaded += download_size;

            send_message(debugTextArea_q, "file downloaded: ");
            send_message(debugTextArea_q, std::to_string(total_downloaded).c_str());
            send_message(debugTextArea_q, "B\n");
        }

        send_message(debugTextArea_q, "total downloaded size: ");
        send_message(debugTextArea_q, std::to_string(total_downloaded).c_str());
        send_message(debugTextArea_q, "B\n");

        send_message(debugTextArea_q, "firmware download complete!\n");

        heap_caps_free(buf);

        new_firmware.close();

        http.end();

        vTaskDelete(nullptr);
    }
}
