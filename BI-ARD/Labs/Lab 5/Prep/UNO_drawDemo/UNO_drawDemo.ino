#include <MCUFRIEND_kbv.h>
MCUFRIEND_kbv tft;
#include <TouchScreen.h>
#define MINPRESSURE 400
#define MAXPRESSURE 1000

const int XP=8,XM=A2,YP=A3,YM=9; //320x480 ID=0x9486
const int TS_LEFT=127,TS_RT=919,TS_TOP=952,TS_BOT=91;

TouchScreen ts = TouchScreen(XP, YP, XM, YM, 300);

#define RED     0xF800
#define BLACK   0x0000

int pixel_x, pixel_y;

void setup() {
    Serial.begin(9600);
    uint16_t ID = tft.readID();
    tft.begin(ID);
    tft.setRotation(0);            //PORTRAIT
    tft.fillScreen(BLACK);
    
}

int last1 =1 , last2 =1 , last3 =1 , last4 =1 , last5 =1, buttonPos = 0;

void loop() {
  // put your main code here, to run repeatedly:
  TSPoint p = ts.getPoint();
    pinMode(YP, OUTPUT);      //restore shared pins
    pinMode(XM, OUTPUT);
    digitalWrite(YP, HIGH);   //because TFT control pins
    digitalWrite(XM, HIGH);
    bool pressed = (p.z > MINPRESSURE && p.z < MAXPRESSURE);
    
    if (pressed) {

      pixel_x = map(p.x, TS_LEFT, TS_RT, 0, tft.width());
      pixel_y = map(p.y, TS_TOP, TS_BOT, 0, tft.height());

      tft.drawPixel(pixel_x,pixel_y,RED);
        
    }
}
