# ☕ ESP-IDF 구성

## ESP-IDF 설치 및 설정

- Visual Studio Code의 ESP-IDF 확장을 통해서도 ESP-IDF를 설치할 수는 있지만, 현재 사용 중인 디스플레이에 맞는 ESP-IDF 툴체인 버전을 사용하기 위해 별도로 ESP-IDF 툴체인을 설치한 후, Visual Studio Code ESP-IDF 확장을 설치하여 개발하도록 함

    - 사용할 ESP-IDF 툴체인 버전은 [4.4.7](https://github.com/espressif/esp-idf/releases/tag/v4.4.7)

- ESP-IDF 설치 후 Visual Studio Code에서 ESP-IDF 확장을 설치

    - 설정 과정에는 USE EXISTING SETUP 옵션을 통해 미리 설치한 툴체인을 통해 확장을 설정
    
    - 칩셋은 esp32s3, COM 포트는 미리 디스플레이를 연결해둔 상태가 아니면 임의로 설정(이후 변경 가능), 빌드 방식은 via Builtin USB-JTAG를 이용

- Visual Studio Code 확장까지 설치한 이후에는 새 프로젝트를 생성하여 설정

    - 제공되는 예제나 빈 프로젝트에서 설정

    - ESP-IDF 터미널(`Ctrl` + `E` + `T`)에서 `idf.py menuconfig`를 통해 ESP-IDF 프로젝트 설정 화면을 열면서 최초 설정 빌드 수행

    - [sdkconfig](../sdkconfig) 파일을 참고하여 설정하거나 해당 파일 자체를 복사하여 붙여넣기

        - PSRAM 및 Arduino 설정 등이 포함


## Arduino as an ESP-IDF component

- 복잡한 로직 개발을 위해 개발 자체는 ESP-IDF를 통해 개발하지만, 그래픽 / 터치 드라이버 코드 등 제공되는 예제들은 모두 Arduino 기반으로 작성되어 있으므로, ESP-IDF에서 Arduino 기능을 사용하기 위해 Arduino core for the ESP32를 이용

- arduino-esp32라는 ESP-IDF 컴포넌트를 이용하며, 디스플레이에 맞는 버전 [2.0.15](https://github.com/espressif/arduino-esp32/releases/tag/2.0.15)를 이용

- 프로젝트 루트 디렉토리에 `components` 디렉토리를 생성하여 아래에 다운받은 arduino-esp32 디렉토리를 저장


## 시리얼 통신 테스트(`test1`)

- 다음 코드를 통해 Arduino core for the ESP32를 테스트

  ```C
  // /main/main.cpp
  #include <Arduino.h> // 필수 포함 헤더

  #define COFFEE_BAUD_RATE 115200 // ESP-IDF 시리얼 모니터에서 출력을 받기 위해서는 Baud Rate을 115200 설정하는 것이 적절(작으면 출력을 못 받음)

  extern "C" void app_main(void)
  {
      initArduino(); // Arduino core for the ESP32를 사용하기 위한 초기화 함수

      Serial.begin(COFFEE_BAUD_RATE);

      Serial.println("Hello, world!");
  }

  ```

- `Ctrl` + `E` + `B`를 통해 빌드

- `Ctrl` + `E` + `F`를 통해 임베디드 보드에 플래싱

- `Ctrl` + `E` + `M`를 통해 시리얼 모니터링
