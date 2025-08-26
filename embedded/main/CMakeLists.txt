idf_component_register(SRCS "src/main.cpp"
        "src/config.cpp"
        "src/debug_task.cpp"
        "src/ipc.cpp"
        "src/network_task.cpp"
        "src/ui_task.cpp"
        "ui/ui_comp_hook.c"
        "ui/ui_configScreen.c"
        "ui/ui_debugScreen.c"
        "ui/ui_dropDoneScreen.c"
        "ui/ui_dropScreen.c"
        "ui/ui_events.cpp"
        "ui/ui_firmwareScreen.c"
        "ui/ui_flavorScreen.c"
        "ui/ui_helpers.c"
        "ui/ui_mainScreen.c"
        "ui/ui_serverScreen.c"
        "ui/ui_wifiScreen.c"
        "ui/ui.c"
    INCLUDE_DIRS "include"
        "ui"
    REQUIRES arduino-esp32
        mbedtls
        cJSON
        coffee-driver
        lvgl
)
