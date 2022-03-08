// Arduino ESPLORA

#include <Esplora.h>
#include <SD.h>
#include <SPI.h>
#include <TFT.h>

void setup() {
  Serial.begin(9600);
  EsploraTFT.begin();
  EsploraTFT.background(0, 0, 0);
  EsploraTFT.stroke(255, 255, 255);
  Esplora.writeRGB(0, 0, 0);

  // SD card check
  if (!SD.begin(8)) {
    Serial.println(F("No SD card inserted!"));
    EsploraTFT.text("No SD card inserted!", 10, 10);
    while (1);
  }
  // file check
  if (!SD.exists("bi-ard.bmp")) {
    Serial.println(F("bi-ard.bmp missing!"));
    EsploraTFT.text("bi-ard.bmp missing!", 10, 10);
    while (1);
  }

  PImage img = EsploraTFT.loadImage("bi-ard.bmp");

  if (img.isValid()) {
    Esplora.writeGreen(255);
    delay(50);
    Esplora.writeGreen(0);
    delay(50);
    Esplora.writeGreen(255);
    delay(50);
    Esplora.writeGreen(0);

    EsploraTFT.image(img, 0, 0);
  } else {

    EsploraTFT.text("bi-ard.bmp is not valid file!", 10, 10);

    Esplora.writeRed(255);
    delay(50);
    Esplora.writeRed(0);
    delay(50);
    Esplora.writeRed(255);
    delay(50);
    Esplora.writeRed(0);
  }
}

void loop() {}
