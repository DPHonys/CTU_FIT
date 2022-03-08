#include <MCUFRIEND_kbv.h>
MCUFRIEND_kbv tft;
#include <TouchScreen.h>
#define MINPRESSURE 400
#define MAXPRESSURE 1000

#include <EEPROM.h>

// display calibration
const int XP = 8, XM = A2, YP = A3, YM = 9; // 320x480 ID=0x9486
const int TS_LEFT = 127, TS_RT = 919, TS_TOP = 952, TS_BOT = 91;

TouchScreen ts = TouchScreen(XP, YP, XM, YM, 300);

#include "lion.h"

// colors
#define RED 0xF800
#define BLACK 0x0000
#define GREEN 0x07E0

// global variables
int last1 = 1, last2 = 1, last3 = 1, last4 = 1, last5 = 1, saveButton = 0;            // debounce variables
int pixel_x, pixel_y, old_pixel_x, old_pixel_y, edge_x, edge_y, button = 1, last = 1; // position variables

void setup() {
  // basic setup of serial and display
  Serial.begin(9600);
  uint16_t ID = tft.readID();
  tft.begin(ID);
  tft.setRotation(0);
  tft.fillScreen(BLACK);

  // save button
  tft.fillRect(0, 0, tft.width(), 40, GREEN);
  tft.setCursor(137, 13);
  tft.setTextColor(BLACK);
  tft.setTextSize(2);
  tft.print("SAVE");
  tft.setCursor(0, 0);

  // EEPROM read
  int readX = map(EEPROM.read(0), 0, 255, 32, 288);
  int readY = map(EEPROM.read(1), 0, 255, 72, 448);

  pixel_x = old_pixel_x = readX;
  pixel_y = old_pixel_y = readY;

  // print lion
  tft.setAddrWindow(pixel_x - 32, pixel_y - 32, pixel_x - 32 + 64 - 1, pixel_y - 32 + 64 - 1);
  tft.pushColors((const uint8_t *)lion, 64 * 64, 1, false);
  tft.setAddrWindow(0, 0, tft.width() - 1, tft.height() - 1);
}

// EEPROM save
void savePos() {
  int saveX = map(old_pixel_x, 32, 288, 0, 255);
  int saveY = map(old_pixel_y, 72, 448, 0, 255);

  EEPROM.update(0, saveX);
  EEPROM.update(1, saveY);
  Serial.println("Position saved!");
}

void loop() {
  // display touch
  TSPoint p = ts.getPoint();
  pinMode(YP, OUTPUT); // restore shared pins
  pinMode(XM, OUTPUT);
  digitalWrite(YP, HIGH); // because TFT control pins
  digitalWrite(XM, HIGH);
  bool pressed = (p.z > MINPRESSURE && p.z < MAXPRESSURE);

  pixel_x = map(p.x, TS_LEFT, TS_RT, 0, tft.width());
  pixel_y = map(p.y, TS_TOP, TS_BOT, 0, tft.height());

  // save button is pressed = debounce starts
  if ((pixel_y <= 40)) {
    saveButton = 1;
    pixel_x = old_pixel_x;
    pixel_y = old_pixel_y;
    button = 0;

    last1 = 0;
    last2 = 0;
    last3 = 0;
    last4 = 0;
    last5 = 0;
  }

  // check if the display is touched
  if (pressed) {
    // edge detection X axis
    if (pixel_x <= 32) {
      pixel_x = 32;
      edge_x = 1;
    } else if (pixel_x >= (tft.width() - 32)) {
      pixel_x = (tft.width() - 32);
      edge_x = 1;
    } else {
      edge_x = 0;
    }

    // edge detection Y axis
    if (pixel_y <= 72 && pixel_y > 40) {
      pixel_y = 72;
      edge_y = 1;
    } else if (pixel_y >= (tft.height() - 32)) {
      pixel_y = (tft.height() - 32);
      edge_y = 1;
    } else {
      edge_y = 0;
    }

    // render lion
    if ((!edge_x && button) || (!edge_y && button)) {
      tft.fillRect(old_pixel_x - 32, old_pixel_y - 32, 64, 64, BLACK);                             // over print old lion
      tft.setAddrWindow(pixel_x - 32, pixel_y - 32, pixel_x - 32 + 64 - 1, pixel_y - 32 + 64 - 1); // move render window
      tft.pushColors((const uint8_t *)lion, 64 * 64, 1, false);                                    // render new one
      tft.setAddrWindow(0, 0, tft.width() - 1, tft.height() - 1);                                  // reset window
      old_pixel_x = pixel_x;
      old_pixel_y = pixel_y;
    }
  } else {
    // debounce part if the display is not touched
    // once no touch is found in last 5 ticks the save function can be saved
    if (!last1) {
      last1 = 1;
    } else if (!last2) {
      last2 = 1;
    } else if (!last3) {
      last3 = 1;
    } else if (!last4) {
      last4 = 1;
    } else if (!last5) {
      last5 = 1;
    }

    if (last1 && last2 && last3 && last4 && last5)
      button = 1;
  }

  // after save button is pressed and released (debounced) the current position is saved into EEPROM
  if (saveButton && button) {
    savePos();
    saveButton = 0;
  }
}
