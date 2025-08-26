#include "ui.h"

#include <string>

#include <Arduino.h>
#include <WiFi.h>

#include <cJSON.h>

#include "coffee/config.hpp"
#include "coffee/debug_task.hpp"
#include "coffee/ipc.hpp"
#include "coffee/network_task.hpp"

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

	/**
	 * @brief FreeRTOS의 큐를 이용하여 wifi 스크린의 wifiTextArea에 Wi-Fi 연결 정보를 표시하는 lvgl 타이머 콜백 함수
	 * 
	 *        lvgl timer callback function that displays Wi-Fi connection information on the wifiTextArea of the Wi-Fi screen using a FreeRTOS queue
	 */
	static void wifi_textArea_timer_cb(lv_timer_t* t);

	/**
	 * @brief FreeRTOS의 큐를 이용하여 debug 스크린의 debugTextArea에 디버깅 정보를 표시하는 lvgl 타이머 콜백 함수
	 * 
	 *        lvgl timer callback function that displays debugging information on the debugTextArea of the debug screen using a FreeRTOS queue
	 */
	static void debug_textArea_timer_cb(lv_timer_t* t);

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

    // wifi 스크린의 wifiTextArea에 Wi-Fi 연결 정보 표현을 위한 lvgl 타이머
    // lvgl timer for displaying Wi-Fi connection information on the wifiTextArea of the Wi-Fi screen
    static lv_timer_t* wifi_textArea_timer = nullptr;

    // debug 스크린의 debugTextArea에 디버깅 정보 표현을 위한 lvgl 타이머
    // lvgl timer for displaying debugging information on the debugTextArea of the debug screen
    static lv_timer_t* debug_textArea_timer = nullptr;

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

        if (status == WL_CONNECTED) {
			std::string wifiLabel = std::string("Wi-Fi: ") + std::string(WiFi.SSID().c_str());

            lv_label_set_text(ui_mainWiFiLabel, wifiLabel.c_str());
        } else {
            lv_label_set_text(ui_mainWiFiLabel, "Wi-Fi: Disconnected");
        }
    }

    static void wifi_info_timer_cb(lv_timer_t* t) {
        if (!ui_wifiInfoLabel || !lv_obj_is_valid(ui_wifiInfoLabel)) {
			return;
		}

        auto status = WiFi.status();

        if (status == WL_CONNECTED) {
            std::string wifiLabel = std::string("Wi-Fi: ") + std::string(WiFi.SSID().c_str());

            lv_label_set_text(ui_wifiInfoLabel, wifiLabel.c_str());

            auto r = WiFi.RSSI();
            if (r >= -60) {
                lv_img_set_src(ui_wifiInfoImage, rssi[2]);
            } else {
                lv_img_set_src(ui_wifiInfoImage, rssi[1]);
            }
        } else {
            lv_label_set_text(ui_wifiInfoLabel, "Wi-Fi: Disconnected");
            lv_img_set_src(ui_wifiInfoImage, rssi[0]);
        }
    }

    static void wifi_textArea_timer_cb(lv_timer_t* t) {
        char buf[COFFEE_MAX_STR_BUF];

        while (receive_message(wifiTextArea_q, buf)) {
            lv_textarea_add_text(ui_wifiTextArea, buf);
        }
    }

    static void debug_textArea_timer_cb(lv_timer_t* t) {
        char buf[COFFEE_MAX_STR_BUF];

        while (receive_message(debugTextArea_q, buf)) {
            lv_textarea_add_text(ui_debugTextArea, buf);
        }
    }
}

extern "C" {
    // mainScreen 로딩 시작 시 호출
    // called when mainScreen loading begins
	void main_screen_load(lv_event_t * e)
	{
		lv_img_set_src(ui_mainConfigImage, "S:/res/icon/main/config.bin");
		lv_img_set_src(ui_mainStartImage, "S:/res/icon/main/start.bin");

		lv_img_set_src(ui_mainImage, coffee::ads[0]);

		if (!coffee::main_ad_timer) {
			coffee::main_ad_timer = lv_timer_create(coffee::main_ad_timer_cb, COFFEE_AD_TERM, nullptr);
		}

		std::string firmwareLabel = std::string("Firmware Ver. ") + std::string(COFFEE_FIRMWARE_VER);

		lv_label_set_text(ui_mainFirmwareLabel, firmwareLabel.c_str());

        if (!coffee::main_wifi_timer) {
            coffee::main_wifi_timer = lv_timer_create(coffee::main_wifi_timer_cb, 1000, nullptr);
        }
	}

    // mainScreen 삭제 시 호출
    // called when mainScreen is deleted
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
    // called when flavorScreen loading begins
	void flavor_screen_load(lv_event_t * e)
	{
		lv_img_set_src(ui_flavorStrawberryImage, "S:/res/icon/flavor/strawberry.bin");
		lv_img_set_src(ui_flavorGreenGrapesImage, "S:/res/icon/flavor/green_grapes.bin");
		lv_img_set_src(ui_flavorPlumImage, "S:/res/icon/flavor/plum.bin");
		lv_img_set_src(ui_flavorBackImage, "S:/res/icon/share/back.bin");
	}

    // flavorScreen의 flavorStrawberryButton 클릭 시 호출
    // called when the flavorStrawberryButton on flavorScreen is clicked
	void flavor_drop_strawberry(lv_event_t * e)
	{
		// 추가 예정
	}

    // flavorScreen의 flavorGreenGrapesButton 클릭 시 호출
    // called when the flavorGreenGrapesButton on flavorScreen is clicked
	void flavor_drop_green_grapes(lv_event_t * e)
	{
		// 추가 예정
	}

    // flavorScreen의 flavorPlumButton 클릭 시 호출
    // called when the flavorPlumButton on flavorScreen is clicked
	void flavor_drop_plum(lv_event_t * e)
	{
		// 추가 예정
	}

    // configScreen 로딩 시작 시 호출
    // called when configScreen loading begins
	void config_screen_load(lv_event_t * e)
	{
		lv_img_set_src(ui_configWifiImage, "S:/res/icon/config/wifi.bin");
		lv_img_set_src(ui_configServerImage, "S:/res/icon/config/server.bin");
		lv_img_set_src(ui_configFirmwareImage, "S:/res/icon/config/firmware.bin");
		lv_img_set_src(ui_configBackImage, "S:/res/icon/share/back.bin");
	}

    // dropScreen 로딩 시작 시 호출
    // called when dropScreen loading begins
	void drop_screen_load(lv_event_t * e)
	{
		lv_img_set_src(ui_dropImage, "S:/res/icon/drop/processing.bin");
		lv_img_set_src(ui_dropBackImage, "S:/res/icon/share/back.bin");
	}

    // dropDoneScreen 로딩 시작 시 호출
    // called when dropDoneScreen loading begins
	void dropDone_screen_load(lv_event_t * e)
	{
		lv_img_set_src(ui_dropDoneImage, "S:/res/icon/dropDone/done.bin");
	}

    // wifiScreen 로딩 시작 시 호출
    // called when wifiScreen loading begins
	void wifi_screen_load(lv_event_t * e)
	{
		lv_img_set_src(ui_wifiInfoImage, "S:/res/icon/wifi/rssi_0.bin");
		lv_img_set_src(ui_wifiBackImage, "S:/res/icon/share/back.bin");
		lv_textarea_set_text(ui_wifiSsidTextArea, "");
		lv_textarea_set_text(ui_wifiPwTextArea, "");

        if (!coffee::wifi_info_timer) {
            coffee::wifi_info_timer = lv_timer_create(coffee::wifi_info_timer_cb, 1000, nullptr);
        }

        if (!coffee::wifi_textArea_timer) {
            coffee::wifi_textArea_timer = lv_timer_create(coffee::wifi_textArea_timer_cb, 100, nullptr);
        }
	}

    // wifiScreen 삭제 시 호출
    // called when wifiScreen is deleted
    void wifi_screen_unload(lv_event_t * e) {
        if (coffee::wifi_info_timer) {
            lv_timer_del(coffee::wifi_info_timer);

            coffee::wifi_info_timer = nullptr;
        }

        if (coffee::wifi_textArea_timer) {
            lv_timer_del(coffee::wifi_textArea_timer);

            coffee::wifi_textArea_timer = nullptr;
        }
    }

    // wifiScreen의 wifiConnectButton 클릭 시 호출
    // called when the wifiConnectButton on wifiScreen is clicked
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
        
        coffee::connect_wifi(ssid, pw);
	}

    // wifiScreen의 wifiRestoreButton 클릭 시 호출
    // called when the wifiRestoreButton on wifiScreen is clicked
	void wifi_restore(lv_event_t * e)
	{
		const char *last_ssid = nullptr, *last_password = nullptr;
		if (coffee::get_last_wifi(last_ssid, last_password)) {
			lv_textarea_set_text(ui_wifiSsidTextArea, last_ssid);
			lv_textarea_set_text(ui_wifiPwTextArea, last_password);

			coffee::connect_wifi(last_ssid, last_password);
		}
	}

    // serverScreen 로딩 시작 시 호출
    // called when serverScreen loading begins
	void server_screen_load(lv_event_t * e)
	{
		lv_img_set_src(ui_serverBackImage, "S:/res/icon/share/back.bin");
	}

	// serverScreen 삭제 시 호출
    // called when serverScreen is deleted
	void server_screen_unload(lv_event_t * e) {
		// 추가 예정
	}

    // serverScreen의 serverSetButton 클릭 시 호출
    // called when the serverSetButton on serverScreen is clicked
	void server_set(lv_event_t * e)
	{
		// 추가 예정
	}

    // firmwareScreen 로딩 시작 시 호출
    // called when firmwareScreen loading begins
	void firmware_screen_load(lv_event_t * e)
	{
		lv_img_set_src(ui_firmwareDebugImage, "S:/res/icon/firmware/debug.bin");
		lv_img_set_src(ui_firmwareBackImage, "S:/res/icon/share/back.bin");

		std::string currentLabel = std::string("Current Version ") + std::string(COFFEE_FIRMWARE_VER);

		lv_label_set_text(ui_firmwareCurrentLabel, currentLabel.c_str());
	}

    // firmwareScreen의 firmwareCheckButton 클릭 시 호출
    // called when the firmwareCheckButton on firmwareScreen is clicked
	void firmware_check(lv_event_t * e)
	{
		// 추가 예정
	}

    // debugScreen 로딩 시작 시 호출
    // called when debugScreen loading begins
	void debug_screen_load(lv_event_t * e)
	{
		lv_img_set_src(ui_debugBackImage, "S:/res/icon/share/back.bin");

        if (!coffee::debug_textArea_timer) {
            coffee::debug_textArea_timer = lv_timer_create(coffee::debug_textArea_timer_cb, 100, nullptr);
        }
	}

	// debugScreen 삭제 시 호출
    // called when debugScreen is deleted
	void debug_screen_unload(lv_event_t * e) {
		if (coffee::debug_textArea_timer) {
            lv_timer_del(coffee::debug_textArea_timer);

            coffee::debug_textArea_timer = nullptr;
        }
	}

    // debugScreen의 debugFunction1Button 클릭 시 호출
    // called when the debugFunction1Button on debugScreen is clicked
	void debug_function1(lv_event_t * e)
	{
		// (v0.0.3-alpha)펌웨어 ID를 읽어 해당하는 펌웨어를 SD 카드에 다운로드하는 함수
        // (v0.0.3-alpha)function to read the firmware ID and download the corresponding firmware to the SD card
		if (WiFi.status() != WL_CONNECTED) {
            lv_textarea_add_text(ui_debugTextArea, "You are not connected to a network\nPlease connect to Wi-Fi first");

            return;
		}

        char* firmware_id = const_cast<char*>(lv_textarea_get_text(ui_debugCmdTextArea));
        if (firmware_id[0] == '\0') {
            lv_textarea_add_text(ui_debugTextArea, "Invalid firmware ID! aborted...");

            return;
        }

        coffee::debug1(reinterpret_cast<void*>(firmware_id));
	}

    // debugScreen의 debugFunction2Button 클릭 시 호출
    // called when the debugFunction2Button on debugScreen is clicked
	void debug_function2(lv_event_t * e)
	{
		// 추가 예정
	}
}
