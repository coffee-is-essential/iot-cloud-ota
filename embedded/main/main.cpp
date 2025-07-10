#include <Arduino.h>
#include <Wire.h>
#include <SPI.h>

#include "lcd_conf.h"

#define BAUD_RATE 115200

/**
 * @brief Initialize LCD
 */
void init_lcd(void);

/**
 * @brief Test serial communication
 */
void test1(void);

/**
 * @brief Test lcd
 */
void test2(void);

PCA9557 out;
LGFX lcd;

extern "C" void app_main(void)
{
    initArduino();
    Serial.begin(BAUD_RATE);
    
    init_lcd();

    // test1();
    test2();
}

void init_lcd(void)
{
    // initialize touch driver
    Wire.begin(19, 20);
    out.reset();
    out.setMode(IO_OUTPUT);
    out.setState(IO0, IO_LOW);
    out.setState(IO1, IO_LOW);
    delay(20);
    out.setState(IO0, IO_HIGH);
    delay(100);
    out.setMode(IO1, IO_INPUT);

    // initialize lcd
    lcd.begin();
    lcd.fillScreen(TFT_BLACK);
    lcd.setTextSize(3);
    delay(100);
}

void test1(void)
{
    Serial.println("Hello, world!");
}

void test2(void)
{
    lcd.fillScreen(TFT_BLUE);
    delay(1000);

    lcd.fillScreen(TFT_YELLOW);
    delay(1000);

    lcd.fillScreen(TFT_GREEN);
    delay(1000);

    lcd.fillScreen(TFT_WHITE);
    delay(1000);

    lcd.fillScreen(TFT_BLACK);

    lcd.fillCircle(100, 100, 50, TFT_YELLOW);

    lcd.setCursor((int32_t) screen_width / 2, (int32_t) screen_height / 2);
    lcd.print("Hello, world!");

    Serial.println("Hi, LCD!");
}
