#include "ui.h"

#include <cstdio>

#include <freertos/FreeRTOS.h>
#include <freertos/queue.h>

#include <Arduino.h>
#include <WiFi.h>

#include <cJSON.h>

#include <coffee_drv/wifi.hpp>

#include "coffee/config.hpp"

namespace coffee {
	/**
	 * @brief 광고 이미지를 변경하는 lvgl 타이머 콜백 함수
	 * 
	 * 		  lvgl timer callback function for switching advertisement images
	 */
	static void main_ad_timer_cb(lv_timer_t* t);

	/**
	 * @brief main 스크린에 Wi-Fi 연결 정보를 표현하는 lvgl 타이머 콜백 함수
     * 
     *        lvgl timer callback function for displaying Wi-Fi connection information on the main screen
	 */
	static void main_wifi_timer_cb(lv_timer_t* t);

    /**
     * @brief wifi 스크린에 Wi-Fi 연결 정보를 표현하는 lvgl 타이머 콜백 함수
     * 
     *        lvgl timer callback function for displaying Wi-Fi connection information on the wifi screen
     */
    static void wifi_info_timer_cb(lv_timer_t* t);

	// 광고 이미지 파일
    // advertisement image file
	static const char* ads[3] = {
		"S:/res/contents/ad1.bin",
		"S:/res/contents/ad2.bin",
		"S:/res/contents/ad3.bin"
	};

    // 현재 표시 중인 광고 인덱스
    // current advertisement index
	static int ad_cnt = 0;

    // 광고 전환을 위한 lvgl 타이머
    // lvgl timer for advertisement switching
	static lv_timer_t* main_ad_timer = nullptr;

	// main 스크린의 Wi-Fi 연결 정보 표현을 위한 lvgl 타이머
    // lvgl timer for displaying Wi-Fi connection information on the main screen
	static lv_timer_t* main_wifi_timer = nullptr;

    // Wi-Fi RSSI 이미지 파일
    // Wi-Fi RSSI image files
    static const char* rssi[3] = {
        "S:/res/icon/wifi/rssi_0.bin",
        "S:/res/icon/wifi/rssi_1.bin",
        "S:/res/icon/wifi/rssi_2.bin"
    };

    // wifi 스크린의 Wi-Fi 연결 정보 표현을 위한 lvgl 타이머
    // lvgl timer for displaying Wi-Fi connection information on the wifi screen
    static lv_timer_t* wifi_info_timer = nullptr;

	static void main_ad_timer_cb(lv_timer_t* t) {
		if (!ui_mainImage || !lv_obj_is_valid(ui_mainImage)) {
			return;
		}

		ad_cnt = (ad_cnt + 1) % 3;
		lv_img_set_src(ui_mainImage, ads[ad_cnt]);
	}

    static void main_wifi_timer_cb(lv_timer_t* t) {
        if (!ui_mainWiFiLabel || !lv_obj_is_valid(ui_mainWiFiLabel)) {
			return;
		}

        auto status = WiFi.status();

        if (status == WL_DISCONNECTED || status == WL_CONNECTION_LOST || status == WL_CONNECT_FAILED) {
            lv_label_set_text(ui_mainWiFiLabel, "Wi-Fi: Disconnected");
        } else if (status == WL_IDLE_STATUS) {
            lv_label_set_text(ui_mainWiFiLabel, "Wi-Fi: Connecting...");
        } else if (status == WL_CONNECTED) {
            char wifiLabel[64];

		    snprintf(wifiLabel, sizeof(wifiLabel), "Wi-Fi: %s", WiFi.SSID().c_str());

            lv_label_set_text(ui_mainWiFiLabel, wifiLabel);
        }
    }

    static void wifi_info_timer_cb(lv_timer_t* t) {
        if (!ui_wifiInfoLabel || !lv_obj_is_valid(ui_wifiInfoLabel)) {
			return;
		}

        auto status = WiFi.status();

        if (status == WL_DISCONNECTED || status == WL_CONNECTION_LOST || status == WL_CONNECT_FAILED) {
            lv_label_set_text(ui_wifiInfoLabel, "Wi-Fi: Disconnected");
            lv_img_set_src(ui_wifiInfoImage, rssi[0]);
        } else if (status == WL_IDLE_STATUS) {
            lv_label_set_text(ui_wifiInfoLabel, "Wi-Fi: Connecting...");
            lv_img_set_src(ui_wifiInfoImage, rssi[0]);
        } else if (status == WL_CONNECTED) {
            char wifiLabel[64];

		    snprintf(wifiLabel, sizeof(wifiLabel), "Wi-Fi: %s", WiFi.SSID().c_str());

            lv_label_set_text(ui_wifiInfoLabel, wifiLabel);

            auto r = WiFi.RSSI();
            if (r >= -60) {
                lv_img_set_src(ui_wifiInfoImage, rssi[2]);
            } else {
                lv_img_set_src(ui_wifiInfoImage, rssi[1]);
            }
        }
    }
}

extern "C" {
	QueueHandle_t wifiTextArea_q;

    // mainScreen 로딩 시작 시 호출
	void main_screen_load(lv_event_t * e)
	{
		lv_img_set_src(ui_mainConfigImage, "S:/res/icon/main/config.bin");
		lv_img_set_src(ui_mainStartImage, "S:/res/icon/main/start.bin");

		lv_img_set_src(ui_mainImage, coffee::ads[0]);

		if (!coffee::main_ad_timer) {
			coffee::main_ad_timer = lv_timer_create(coffee::main_ad_timer_cb, COFFEE_AD_TERM, nullptr);
		}

		char firmwareLabel[64];

		snprintf(firmwareLabel, sizeof(firmwareLabel), "Firmware Ver. %s", COFFEE_FIRMWARE_VER);

		lv_label_set_text(ui_mainFirmwareLabel, firmwareLabel);

        if (!coffee::main_wifi_timer) {
            coffee::main_wifi_timer = lv_timer_create(coffee::main_wifi_timer_cb, 1000, nullptr);
        }
	}

    // mainScreen 삭제 시 호출
	void main_screen_unload(lv_event_t * e)
	{
		if (coffee::main_ad_timer) {
			lv_timer_del(coffee::main_ad_timer);

			coffee::main_ad_timer = nullptr;
		}

        if (coffee::main_wifi_timer) {
            lv_timer_del(coffee::main_wifi_timer);

            coffee::main_wifi_timer = nullptr;
        }
	}

    // flavorScreen 로딩 시작 시 호출
	void flavor_screen_load(lv_event_t * e)
	{
		lv_img_set_src(ui_flavorStrawberryImage, "S:/res/icon/flavor/strawberry.bin");
		lv_img_set_src(ui_flavorGreenGrapesImage, "S:/res/icon/flavor/green_grapes.bin");
		lv_img_set_src(ui_flavorPlumImage, "S:/res/icon/flavor/plum.bin");
		lv_img_set_src(ui_flavorBackImage, "S:/res/icon/share/back.bin");
	}

    // flavorScreen의 flavorStrawberryButton 클릭 시 호출
	void flavor_drop_strawberry(lv_event_t * e)
	{
		// 추가 예정
	}

    // flavorScreen의 flavorGreenGrapesButton 클릭 시 호출
	void flavor_drop_green_grapes(lv_event_t * e)
	{
		// 추가 예정
	}

    // flavorScreen의 flavorPlumButton 클릭 시 호출
	void flavor_drop_plum(lv_event_t * e)
	{
		// 추가 예정
	}

    // configScreen 로딩 시작 시 호출
	void config_screen_load(lv_event_t * e)
	{
		lv_img_set_src(ui_configWifiImage, "S:/res/icon/config/wifi.bin");
		lv_img_set_src(ui_configServerImage, "S:/res/icon/config/server.bin");
		lv_img_set_src(ui_configFirmwareImage, "S:/res/icon/config/firmware.bin");
		lv_img_set_src(ui_configBackImage, "S:/res/icon/share/back.bin");
	}

    // dropScreen 로딩 시작 시 호출
	void drop_screen_load(lv_event_t * e)
	{
		lv_img_set_src(ui_dropImage, "S:/res/icon/drop/processing.bin");
		lv_img_set_src(ui_dropBackImage, "S:/res/icon/share/back.bin");
	}

    // dropDoneScreen 로딩 시작 시 호출
	void dropDone_screen_load(lv_event_t * e)
	{
		lv_img_set_src(ui_dropDoneImage, "S:/res/icon/dropDone/done.bin");
	}

    // wifiScreen 로딩 시작 시 호출
	void wifi_screen_load(lv_event_t * e)
	{
		lv_img_set_src(ui_wifiBackImage, "S:/res/icon/share/back.bin");

        if (!coffee::wifi_info_timer) {
            coffee::wifi_info_timer = lv_timer_create(coffee::wifi_info_timer_cb, 1000, nullptr);
        }
	}

    // wifiScreen 삭제 시 호출
    void wifi_screen_unload(lv_event_t * e) {
        if (coffee::wifi_info_timer) {
            lv_timer_del(coffee::wifi_info_timer);

            coffee::wifi_info_timer = nullptr;
        }
    }

    // wifiScreen의 wifiConnectButton 클릭 시 호출
	void wifi_connect(lv_event_t * e)
	{
		const char* ssid = lv_textarea_get_text(ui_wifiSsidTextArea);

        int ssid_len = strlen(ssid);
        if (ssid_len <= 0 || ssid_len >= 32) {
            lv_textarea_add_text(ui_wifiTextArea, "Invalid SSID! aborted...\n");

            return;
        }

		const char* pw = lv_textarea_get_text(ui_wifiPwTextArea);
        int pw_len = strlen(pw);
        if (pw_len < 8 || pw_len >= 64) {
            lv_textarea_add_text(ui_wifiTextArea, "Invalid password! aborted...\n");

            return;
        }
        
        lv_textarea_add_text(ui_wifiTextArea, "Connecting...\n");

		if (coffee_drv::init_wifi_sta(ssid, pw)) {
            lv_textarea_add_text(ui_wifiTextArea, "Connected! IP address=");
            lv_textarea_add_text(ui_wifiTextArea, WiFi.localIP().toString().c_str());
            lv_textarea_add_text(ui_wifiTextArea, "\n");
        }
	}

    // wifiScreen의 wifiRestoreButton 클릭 시 호출
	void wifi_restore(lv_event_t * e)
	{
		const char *last_ssid = nullptr, *last_password = nullptr;
		if (coffee::get_last_wifi(&last_ssid, &last_password)) {
			lv_textarea_set_text(ui_wifiSsidTextArea, last_ssid);
			lv_textarea_set_text(ui_wifiPwTextArea, last_password);

			coffee_drv::init_wifi_sta(last_ssid, last_password);
		}
	}

    // serverScreen 로딩 시작 시 호출
	void server_screen_load(lv_event_t * e)
	{
		lv_img_set_src(ui_serverBackImage, "S:/res/icon/share/back.bin");
	}

    // serverScreen의 serverSetButton 클릭 시 호출
	void server_set(lv_event_t * e)
	{
		// 추가 예정
	}

    // firmwareScreen 로딩 시작 시 호출
	void firmware_screen_load(lv_event_t * e)
	{
		lv_img_set_src(ui_firmwareDebugImage, "S:/res/icon/firmware/debug.bin");
		lv_img_set_src(ui_firmwareBackImage, "S:/res/icon/share/back.bin");

		char currentLabel[64];

		snprintf(currentLabel, sizeof(currentLabel), "Current Version %s", COFFEE_FIRMWARE_VER);

		lv_label_set_text(ui_firmwareCurrentLabel, currentLabel);
	}

    // firmwareScreen의 firmwareCheckButton 클릭 시 호출
	void firmware_check(lv_event_t * e)
	{
		// 추가 예정
	}

    // debugScreen 로딩 시작 시 호출
	void debug_screen_load(lv_event_t * e)
	{
		lv_img_set_src(ui_debugBackImage, "S:/res/icon/share/back.bin");
	}

    // debugScreen의 debugFunction1Button 클릭 시 호출
	void debug_function1(lv_event_t * e)
	{
		// 추가 예정
	}

    // debugScreen의 debugFunction2Button 클릭 시 호출
	void debug_function2(lv_event_t * e)
	{
		// 추가 예정
	}
}
