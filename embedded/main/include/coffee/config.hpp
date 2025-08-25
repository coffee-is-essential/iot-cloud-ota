#ifndef COFFEE_MAIN_CONFIG_HPP
#define COFFEE_MAIN_CONFIG_HPP

/**
 * @def COFFEE_FIRMWARE_NUM
 * 
 * @def COFFEE_ALPHA_TEST
 * 
 * @def COFFEE_BETA_TEST
 * 
 * @def COFFEE_FIRMWARE_VER
 * 
 * @brief 펌웨어 버전 정보
 * 
 *        firmware version information
 */

#define COFFEE_FIRMWARE_NUM "0.0.2"
#define COFFEE_ALPHA_TEST 1
#define COFFEE_BETA_TEST  0

#if (COFFEE_ALPHA_TEST && COFFEE_BETA_TEST)
  #error "only one of COFFEE_ALPHA_TEST or COFFEE_BETA_TEST can be 1"
#endif

#if COFFEE_ALPHA_TEST
  #define COFFEE_FIRMWARE_VER COFFEE_FIRMWARE_NUM " alpha"
#elif COFFEE_BETA_TEST
  #define COFFEE_FIRMWARE_VER COFFEE_FIRMWARE_NUM " beta"
#else
  #define COFFEE_FIRMWARE_VER COFFEE_FIRMWARE_NUM
#endif

/**
 * @def COFFEE_AD_TERM
 * 
 * @brief 광고 이미지 변경 주기(ms)
 * 
 *        interval for switching advertisement images in milliseconds
 */
#define COFFEE_AD_TERM 5000

/**
 * @def COFFEE_MAX_STR_BUF
 * 
 * @brief 문자열 버퍼 최대 크기
 * 
 *        maximum size of the string buffer
 */
#define COFFEE_MAX_STR_BUF 256

#include <esp_heap_caps.h>

#include <Arduino.h>
#include <FS.h>
#include <SD.h>

#include <cJSON.h>

#define COFFEE_CONFIG_JSON_MAX_BYTES 1024

namespace coffee {
    /**
     * @brief JSON 파일로부터 네트워크 정보를 읽어 처리합니다
     * 
     *        reads and processes network information from a JSON file
     */
    void init_network_config(void);

    /**
     * @brief JSON 파일로부터 마지막 Wi-Fi 연결 정보를 불러옵니다
     * 
     *        loads the last Wi-Fi connection information from a JSON file
     * 
     * @param[out] ssid_out 마지막으로 연결된 Wi-Fi SSID
     * 
     *                      the SSID of the last connected Wi-Fi
     * 
     * @param[out] pw_out 마지막으로 연결된 Wi-Fi 비밀번호
     * 
     *                    the password of the last connected Wi-Fi
     * 
     * @return 마지막 Wi-Fi 연결 정보 불러오기 성공 여부
     * 
     *         whether loading the last Wi-Fi connection information succeeded
     */
    bool get_last_wifi(const char** ssid_out, const char** pw_out);

    extern cJSON* config_root;

    extern cJSON* wifi_config;

    extern cJSON* mqtt_config;
}
#endif
