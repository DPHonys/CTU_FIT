// Arduino ESPLORA

#include <TFT.h> // Hardware-specific library
#include <SPI.h>
#include <Esplora.h>

void resetMENU() {
  EsploraTFT.background(0,0,0); // clear the screen
  EsploraTFT.stroke(255,255,255);

  EsploraTFT.text("Select an option:",10,10);
  EsploraTFT.text("1. Temperature",20,30);
  EsploraTFT.text("2. Microphone",20,40);
  EsploraTFT.text("3. Akcelerometer",20,50);
}

void setup() {
  EsploraTFT.begin();
  resetMENU();  
  Serial.begin(9600);
  EsploraTFT.text(">",10,30);
}

int pushed = 0, pos = 30;

void loop() {

  int button1 = Esplora.readButton(SWITCH_1);
  int button2 = Esplora.readButton(SWITCH_4);

  if (button1 == LOW && pos != 50) {
    EsploraTFT.stroke(0,0,0);
    EsploraTFT.text(">",10,pos);
    EsploraTFT.stroke(255,255,255);
    pos+=10;
    EsploraTFT.text(">",10,pos);
  }

  if (button2 == LOW && pos!= 30) {
    EsploraTFT.stroke(0,0,0);
    EsploraTFT.text(">",10,pos);
    EsploraTFT.stroke(255,255,255);
    pos-=10;
    EsploraTFT.text(">",10,pos);
  }
}
