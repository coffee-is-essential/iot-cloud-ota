// This file was generated by SquareLine Studio
// SquareLine Studio version: SquareLine Studio 1.5.3
// LVGL version: 8.3.11
// Project name: wifi-test

#include "ui.h"

lv_obj_t * ui_mainScreen = NULL;
lv_obj_t * ui_title = NULL;
lv_obj_t * ui_connectButton = NULL;
lv_obj_t * ui_connectButtonLabel = NULL;
lv_obj_t * ui_ssidContainer = NULL;
lv_obj_t * ui_ssidLabel = NULL;
lv_obj_t * ui_ssidTextArea = NULL;
lv_obj_t * ui_pwContainer = NULL;
lv_obj_t * ui_pwLabel = NULL;
lv_obj_t * ui_pwTextArea = NULL;
lv_obj_t * ui_chatTextArea = NULL;
lv_obj_t * ui_sendButton = NULL;
lv_obj_t * ui_sendLabel = NULL;
lv_obj_t * ui_keyboard = NULL;
// event funtions
void ui_event_connectButton(lv_event_t * e)
{
    lv_event_code_t event_code = lv_event_get_code(e);

    if(event_code == LV_EVENT_RELEASED) {
        coffee_connect_wifi(e);
    }
}

void ui_event_ssidTextArea(lv_event_t * e)
{
    lv_event_code_t event_code = lv_event_get_code(e);

    if(event_code == LV_EVENT_FOCUSED) {
        keyboardUp_Animation(ui_keyboard, 0);
        _ui_keyboard_set_target(ui_keyboard,  ui_ssidTextArea);
    }
    if(event_code == LV_EVENT_DEFOCUSED) {
        keyboardDown_Animation(ui_keyboard, 0);
    }
}

void ui_event_pwTextArea(lv_event_t * e)
{
    lv_event_code_t event_code = lv_event_get_code(e);

    if(event_code == LV_EVENT_FOCUSED) {
        keyboardUp_Animation(ui_keyboard, 0);
        _ui_keyboard_set_target(ui_keyboard,  ui_pwTextArea);
    }
    if(event_code == LV_EVENT_DEFOCUSED) {
        keyboardDown_Animation(ui_keyboard, 0);
    }
}

void ui_event_sendButton(lv_event_t * e)
{
    lv_event_code_t event_code = lv_event_get_code(e);

    if(event_code == LV_EVENT_RELEASED) {
        coffee_send_ok(e);
    }
}

// build funtions

void ui_mainScreen_screen_init(void)
{
    ui_mainScreen = lv_obj_create(NULL);
    lv_obj_clear_flag(ui_mainScreen, LV_OBJ_FLAG_SCROLLABLE);      /// Flags

    ui_title = lv_label_create(ui_mainScreen);
    lv_obj_set_width(ui_title, LV_SIZE_CONTENT);   /// 1
    lv_obj_set_height(ui_title, LV_SIZE_CONTENT);    /// 1
    lv_obj_set_x(ui_title, -300);
    lv_obj_set_y(ui_title, -200);
    lv_obj_set_align(ui_title, LV_ALIGN_CENTER);
    lv_label_set_text(ui_title, "Connecting Wi-Fi");

    ui_connectButton = lv_btn_create(ui_mainScreen);
    lv_obj_set_width(ui_connectButton, 200);
    lv_obj_set_height(ui_connectButton, 100);
    lv_obj_set_x(ui_connectButton, -165);
    lv_obj_set_y(ui_connectButton, 152);
    lv_obj_set_align(ui_connectButton, LV_ALIGN_CENTER);
    lv_obj_add_flag(ui_connectButton, LV_OBJ_FLAG_SCROLL_ON_FOCUS);     /// Flags
    lv_obj_clear_flag(ui_connectButton, LV_OBJ_FLAG_SCROLLABLE);      /// Flags

    ui_connectButtonLabel = lv_label_create(ui_connectButton);
    lv_obj_set_width(ui_connectButtonLabel, LV_SIZE_CONTENT);   /// 1
    lv_obj_set_height(ui_connectButtonLabel, LV_SIZE_CONTENT);    /// 1
    lv_obj_set_align(ui_connectButtonLabel, LV_ALIGN_CENTER);
    lv_label_set_text(ui_connectButtonLabel, "Connect");
    lv_obj_set_style_text_font(ui_connectButtonLabel, &lv_font_montserrat_40, LV_PART_MAIN | LV_STATE_DEFAULT);

    ui_ssidContainer = lv_obj_create(ui_mainScreen);
    lv_obj_remove_style_all(ui_ssidContainer);
    lv_obj_set_width(ui_ssidContainer, 400);
    lv_obj_set_height(ui_ssidContainer, 75);
    lv_obj_set_x(ui_ssidContainer, -175);
    lv_obj_set_y(ui_ssidContainer, -100);
    lv_obj_set_align(ui_ssidContainer, LV_ALIGN_CENTER);
    lv_obj_clear_flag(ui_ssidContainer, LV_OBJ_FLAG_CLICKABLE | LV_OBJ_FLAG_SCROLLABLE);      /// Flags

    ui_ssidLabel = lv_label_create(ui_ssidContainer);
    lv_obj_set_width(ui_ssidLabel, LV_SIZE_CONTENT);   /// 1
    lv_obj_set_height(ui_ssidLabel, LV_SIZE_CONTENT);    /// 1
    lv_obj_set_x(ui_ssidLabel, -150);
    lv_obj_set_y(ui_ssidLabel, 0);
    lv_obj_set_align(ui_ssidLabel, LV_ALIGN_CENTER);
    lv_label_set_text(ui_ssidLabel, "SSID");
    lv_obj_set_style_text_font(ui_ssidLabel, &lv_font_montserrat_24, LV_PART_MAIN | LV_STATE_DEFAULT);

    ui_ssidTextArea = lv_textarea_create(ui_ssidContainer);
    lv_obj_set_width(ui_ssidTextArea, 300);
    lv_obj_set_height(ui_ssidTextArea, LV_SIZE_CONTENT);    /// 0
    lv_obj_set_x(ui_ssidTextArea, 50);
    lv_obj_set_y(ui_ssidTextArea, 0);
    lv_obj_set_align(ui_ssidTextArea, LV_ALIGN_CENTER);
    lv_textarea_set_placeholder_text(ui_ssidTextArea, "SSID");
    lv_textarea_set_one_line(ui_ssidTextArea, true);
    lv_obj_clear_flag(ui_ssidTextArea, LV_OBJ_FLAG_SCROLLABLE);      /// Flags
    lv_obj_set_style_text_font(ui_ssidTextArea, &lv_font_montserrat_24, LV_PART_MAIN | LV_STATE_DEFAULT);

    ui_pwContainer = lv_obj_create(ui_mainScreen);
    lv_obj_remove_style_all(ui_pwContainer);
    lv_obj_set_width(ui_pwContainer, 400);
    lv_obj_set_height(ui_pwContainer, 75);
    lv_obj_set_x(ui_pwContainer, -175);
    lv_obj_set_y(ui_pwContainer, 0);
    lv_obj_set_align(ui_pwContainer, LV_ALIGN_CENTER);
    lv_obj_clear_flag(ui_pwContainer, LV_OBJ_FLAG_CLICKABLE | LV_OBJ_FLAG_SCROLLABLE);      /// Flags

    ui_pwLabel = lv_label_create(ui_pwContainer);
    lv_obj_set_width(ui_pwLabel, LV_SIZE_CONTENT);   /// 1
    lv_obj_set_height(ui_pwLabel, LV_SIZE_CONTENT);    /// 1
    lv_obj_set_x(ui_pwLabel, -150);
    lv_obj_set_y(ui_pwLabel, 0);
    lv_obj_set_align(ui_pwLabel, LV_ALIGN_CENTER);
    lv_label_set_text(ui_pwLabel, "PW");
    lv_obj_set_style_text_font(ui_pwLabel, &lv_font_montserrat_24, LV_PART_MAIN | LV_STATE_DEFAULT);

    ui_pwTextArea = lv_textarea_create(ui_pwContainer);
    lv_obj_set_width(ui_pwTextArea, 300);
    lv_obj_set_height(ui_pwTextArea, LV_SIZE_CONTENT);    /// 0
    lv_obj_set_x(ui_pwTextArea, 50);
    lv_obj_set_y(ui_pwTextArea, 0);
    lv_obj_set_align(ui_pwTextArea, LV_ALIGN_CENTER);
    lv_textarea_set_placeholder_text(ui_pwTextArea, "Password");
    lv_textarea_set_one_line(ui_pwTextArea, true);
    lv_textarea_set_password_mode(ui_pwTextArea, true);
    lv_obj_clear_flag(ui_pwTextArea, LV_OBJ_FLAG_SCROLLABLE);      /// Flags
    lv_obj_set_style_text_font(ui_pwTextArea, &lv_font_montserrat_24, LV_PART_MAIN | LV_STATE_DEFAULT);

    ui_chatTextArea = lv_textarea_create(ui_mainScreen);
    lv_obj_set_width(ui_chatTextArea, 280);
    lv_obj_set_height(ui_chatTextArea, 325);
    lv_obj_set_x(ui_chatTextArea, 215);
    lv_obj_set_y(ui_chatTextArea, -35);
    lv_obj_set_align(ui_chatTextArea, LV_ALIGN_CENTER);
    lv_textarea_set_text(ui_chatTextArea, "Welcome!");

    ui_sendButton = lv_btn_create(ui_mainScreen);
    lv_obj_set_width(ui_sendButton, 280);
    lv_obj_set_height(ui_sendButton, 50);
    lv_obj_set_x(ui_sendButton, 215);
    lv_obj_set_y(ui_sendButton, 175);
    lv_obj_set_align(ui_sendButton, LV_ALIGN_CENTER);
    lv_obj_add_flag(ui_sendButton, LV_OBJ_FLAG_SCROLL_ON_FOCUS);     /// Flags
    lv_obj_clear_flag(ui_sendButton, LV_OBJ_FLAG_SCROLLABLE);      /// Flags

    ui_sendLabel = lv_label_create(ui_sendButton);
    lv_obj_set_width(ui_sendLabel, LV_SIZE_CONTENT);   /// 1
    lv_obj_set_height(ui_sendLabel, LV_SIZE_CONTENT);    /// 1
    lv_obj_set_align(ui_sendLabel, LV_ALIGN_CENTER);
    lv_label_set_text(ui_sendLabel, "Send OK");
    lv_obj_set_style_text_font(ui_sendLabel, &lv_font_montserrat_24, LV_PART_MAIN | LV_STATE_DEFAULT);

    ui_keyboard = lv_keyboard_create(ui_mainScreen);
    lv_obj_set_width(ui_keyboard, 800);
    lv_obj_set_height(ui_keyboard, 200);
    lv_obj_set_x(ui_keyboard, 0);
    lv_obj_set_y(ui_keyboard, 335);
    lv_obj_set_align(ui_keyboard, LV_ALIGN_CENTER);

    lv_obj_add_event_cb(ui_connectButton, ui_event_connectButton, LV_EVENT_ALL, NULL);
    lv_obj_add_event_cb(ui_ssidTextArea, ui_event_ssidTextArea, LV_EVENT_ALL, NULL);
    lv_obj_add_event_cb(ui_pwTextArea, ui_event_pwTextArea, LV_EVENT_ALL, NULL);
    lv_obj_add_event_cb(ui_sendButton, ui_event_sendButton, LV_EVENT_ALL, NULL);

}

void ui_mainScreen_screen_destroy(void)
{
    if(ui_mainScreen) lv_obj_del(ui_mainScreen);

    // NULL screen variables
    ui_mainScreen = NULL;
    ui_title = NULL;
    ui_connectButton = NULL;
    ui_connectButtonLabel = NULL;
    ui_ssidContainer = NULL;
    ui_ssidLabel = NULL;
    ui_ssidTextArea = NULL;
    ui_pwContainer = NULL;
    ui_pwLabel = NULL;
    ui_pwTextArea = NULL;
    ui_chatTextArea = NULL;
    ui_sendButton = NULL;
    ui_sendLabel = NULL;
    ui_keyboard = NULL;

}
