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

    bool get_last_wifi(const char*& ssid_out, const char*& pw_out) {
        if (!config_root) {
            Serial.println("network_task: config_json has not been initialized");

            ssid_out = nullptr;
            pw_out = nullptr;

            return false;
        }

        if (!cJSON_IsTrue(cJSON_GetObjectItem(wifi_config, "auto_connect"))) {
            ssid_out = nullptr;
            pw_out = nullptr;

            return false;
        }

        cJSON* last_ssid = cJSON_GetObjectItem(wifi_config, "last_ssid");
        if (!last_ssid) {
            Serial.println("network_task: unable to find ‘last_ssid’ object");

            ssid_out = nullptr;
            pw_out = nullptr;

            return false;
        } else if (!cJSON_IsString(last_ssid)) {
            Serial.println("network_task: invalid ‘last_ssid’");

            ssid_out = nullptr;
            pw_out = nullptr;

            return false;
        } else {
            ssid_out = last_ssid->valuestring;
        }

        cJSON* last_password = cJSON_GetObjectItem(wifi_config, "last_password");
        if (!last_password) {
            Serial.println("network_task: unable to find ‘last_password’ object");

            ssid_out = nullptr;
            pw_out = nullptr;

            return false;
        } else if (!cJSON_IsString(last_password)) {
            Serial.println("network_task: invalid ‘last_password’");

            ssid_out = nullptr;
            pw_out = nullptr;

            return false;
        } else {
            pw_out = last_password->valuestring;
        }

        return true;
    }

    bool write_config(void) {
        char* out_config = cJSON_Print(config_root);
        if(!out_config) {
            Serial.println("config: failed to allocate out_config");

            free(out_config);

            return false;
        }

        File file = SD.open("/res/tmp.json", FILE_WRITE);

        if (!file) {
            Serial.println("config: overwriting file failure");

            free(out_config);

            return false;
        }

        file.print(out_config);

        file.close();

        if (!SD.remove("/res/network_config.json")) {
            Serial.println("config: falied to remove the old file");
            Serial.print("    temporary file path: /res/tmp.json");

            free(out_config);

            return false;
        } else {
            if (!SD.rename("/res/tmp.json", "/res/network_config.json")) {
                Serial.println("config: rename failed");
                Serial.print("    temporary file path: /res/tmp.json");

                free(out_config);

                return false;
            }
        }

        Serial.println("config: writing new json file success!");

        free(out_config);

        return true;
    }
}
