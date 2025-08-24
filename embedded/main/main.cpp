#include <Arduino.h>

#include <driver.hpp>

#include "config.hpp"
#include "task.hpp"

extern "C" void app_main(void) {
    initArduino();
    Serial.begin(115200);

    Serial.printf("iot-cloud-ota capstone design: ver. %s\n", COFFEE_FIRMWARE_VER);

    if (!coffee::init_drivers()) {
        return;
    }

    coffee::set_mem_monitor(5000);

    coffee::init_network_config();

    coffee::init_gui();
}
