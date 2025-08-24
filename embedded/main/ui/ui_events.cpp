#include "ui.h"

#include <cstdio>

// 디버깅
// #include <Arduino.h>

#include "config.hpp"

namespace coffee {
	/**
	 * @brief lvgl 타이머의 콜백 함수로 광고 이미지를 변경합니다
	 * 
	 * 	      changes the advertisement image using an lvgl timer callback function
	 */
	static void ad_timer_cb(lv_timer_t* t);

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
	static lv_timer_t* ad_timer = NULL;

	static void ad_timer_cb(lv_timer_t* t) {
		if (!ui_mainImage || lv_scr_act() != ui_mainScreen) {
			return;
		}

		ad_cnt = (ad_cnt + 1) % 3;
		lv_img_set_src(ui_mainImage, ads[ad_cnt]);
	}
}

extern "C" {
    // mainScreen 로딩 시작 시 호출
	void main_screen_load(lv_event_t * e)
	{
		lv_img_set_src(ui_mainConfigImage, "S:/res/icon/main/config.bin");
		lv_img_set_src(ui_mainStartImage, "S:/res/icon/main/start.bin");

		lv_img_set_src(ui_mainImage, coffee::ads[0]);

		if (!coffee::ad_timer) {
			coffee::ad_timer = lv_timer_create(coffee::ad_timer_cb, COFFEE_AD_TERM, NULL);
		}

		char firmwareLabel[64];

		snprintf(firmwareLabel, sizeof(firmwareLabel), "Firmware Ver. %s", COFFEE_FIRMWARE_VER);

		lv_label_set_text(ui_mainFirmwareLabel, firmwareLabel);
	}

    // mainScreen 삭제 시 호출
	void main_screen_unload(lv_event_t * e)
	{
		if (coffee::ad_timer) {
			lv_timer_del(coffee::ad_timer);

			coffee::ad_timer = NULL;
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
		lv_img_set_src(ui_wifiInfoImage, "S:/res/icon/wifi/wifi_0.bin");
		lv_img_set_src(ui_wifiBackImage, "S:/res/icon/share/back.bin");
	}

    // wifiScreen의 wifiConnectButton 클릭 시 호출
	void wifi_connect(lv_event_t * e)
	{
		// 추가 예정
	}

    // wifiScreen의 wifiRestoreButton 클릭 시 호출
	void wifi_restore(lv_event_t * e)
	{
		// 추가 예정
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

		sniprintf(currentLabel, sizeof(currentLabel), "Current Version %s", COFFEE_FIRMWARE_VER);

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
