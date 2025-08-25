#include <Arduino.h>

#include <coffee_drv/init.hpp>
// #include <coffee_drv/utility.hpp>
#include <coffee_drv/wifi.hpp>

#include "coffee/config.hpp"
#include "coffee/network_task.hpp"
#include "coffee/ui_task.hpp"

extern "C" void app_main(void) {
    initArduino();
    Serial.begin(115200);

    Serial.printf("iot-cloud-ota capstone design: ver. %s\n", COFFEE_FIRMWARE_VER);

    if (!coffee_drv::init_drivers()) {
        return;
    }

    // coffee_drv::set_mem_monitor(5000);

    coffee::init_ui_task();

    coffee::init_network_config();

    coffee::restore_wifi();
}
