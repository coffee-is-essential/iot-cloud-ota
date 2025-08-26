#include <Arduino.h>

#include <coffee_drv/init.hpp>
#include <coffee_drv/wifi.hpp>

#include "coffee/config.hpp"
#include "coffee/ipc.hpp"
#include "coffee/ui_task.hpp"

extern "C" void app_main(void) {
    initArduino();
    Serial.begin(115200);

    Serial.printf("iot-cloud-ota capstone design: ver. %s\n", COFFEE_FIRMWARE_VER);

    if (!coffee_drv::init_drivers()) {
        return;
    }

    coffee::init_ipc_queue();

    coffee::init_ui_task();

    coffee::init_network_config();

    const char *last_ssid = nullptr, *last_password = nullptr;
    if (coffee::get_last_wifi(last_ssid, last_password)) {
        coffee_drv::init_wifi_sta(last_ssid, last_password);
    }
}
