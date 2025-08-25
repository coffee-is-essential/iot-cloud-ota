#include "coffee/config.hpp"

namespace coffee {
    /**
     * @brief 모든 설정을 포함하는 JSON 루트 객체
     * 
     *        JSON root object containing all configuration settings
     */
    cJSON* config_root = nullptr;

    /**
     * @brief Wi-Fi 설정을 포함하는 JSON 객체
     * 
     *        JSON object containing Wi-Fi configuration
     */
    cJSON* wifi_config = nullptr;

    /**
     * @brief MQTT 서버 설정을 포함하는 JSON 객체
     * 
     *        JSON object containing MQTT server configuration
     */
    cJSON* mqtt_config = nullptr;

    void init_network_config(void) {
        File config_file = SD.open("/res/network_config.json", FILE_READ);

        if (!config_file) {
            Serial.println("config: reading file failure");

            return;
        }

        char* json_buf = (char*) heap_caps_malloc(COFFEE_CONFIG_JSON_MAX_BYTES, MALLOC_CAP_SPIRAM);

        if (!json_buf) {
            Serial.println("config: allocating buffer failure");

            return;
        }

        size_t bytes = config_file.readBytes(json_buf, COFFEE_CONFIG_JSON_MAX_BYTES - 1);

        json_buf[bytes] = '\0';

        config_file.close();

        config_root = cJSON_Parse(json_buf);

        heap_caps_free(json_buf);

        if (!config_root) {
            Serial.println("config: parsing json failure");

            return;
        }

        wifi_config = cJSON_GetObjectItem(config_root, "wifi");
        if (!wifi_config) {
            Serial.println("config: unable to find ‘wifi’ object");

            cJSON_Delete(config_root);

            return;
        }

        mqtt_config = cJSON_GetObjectItem(config_root, "mqtt");
        if (!mqtt_config) {
            Serial.println("config: unable to find ‘mqtt’ object");

            cJSON_Delete(config_root);

            return;
        }
    }

    bool get_last_wifi(const char** ssid_out, const char** pw_out) {
        if (!config_root) {
            Serial.println("network_task: config_json has not been initialized");

            *ssid_out = nullptr;
            *pw_out = nullptr;

            return false;
        }

        if (!cJSON_IsTrue(cJSON_GetObjectItem(wifi_config, "auto_connect"))) {
            *ssid_out = nullptr;
            *pw_out = nullptr;

            return false;
        }

        *ssid_out = cJSON_GetObjectItem(wifi_config, "last_ssid")->valuestring;
        if (!ssid_out) {
            Serial.println("network_task: unable to find ‘last_ssid’ object");

            *ssid_out = nullptr;
            *pw_out = nullptr;

            return false;
        }

        *pw_out = cJSON_GetObjectItem(wifi_config, "last_password")->valuestring;
        if (!pw_out) {
            Serial.println("network_task: unable to find ‘last_password’ object");

            *ssid_out = nullptr;
            *pw_out = nullptr;

            return false;
        }

        return true;
    }
}
