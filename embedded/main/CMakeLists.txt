idf_component_register(SRCS "main.cpp"
        "config.cpp"
        "task/gui_task.cpp"
        "ui/ui_mainScreen.c"
        "ui/ui_flavorScreen.c"
        "ui/ui_configScreen.c"
        "ui/ui_dropScreen.c"
        "ui/ui_dropDoneScreen.c"
        "ui/ui_wifiScreen.c"
        "ui/ui_serverScreen.c"
        "ui/ui_firmwareScreen.c"
        "ui/ui_debugScreen.c"
        "ui/ui.c"
        "ui/ui_comp_hook.c"
        "ui/ui_helpers.c"
        "ui/ui_events.cpp"
    INCLUDE_DIRS "."
        "task"
        "ui"
    REQUIRES arduino-esp32
        cJSON
        coffee-driver
        lvgl
)
