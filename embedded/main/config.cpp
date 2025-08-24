#include "config.hpp"

namespace coffee {
    cJSON* network_config;

    void init_network_config(void) {
        File config_file = SD.open("/res/network_config.json", FILE_READ);

        if (!config_file) {
            Serial.println("error: reading file failure");

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

        network_config = cJSON_Parse(json_buf);

        heap_caps_free(json_buf);

        if (!network_config) {
            Serial.println("error: parsing json failure");

            return;
        }
    }
}
