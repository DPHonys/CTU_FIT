// Arduino ESPLORA

#include <Esplora.h>

#define BUTTON1 SWITCH_1
#define BUTTON2 SWITCH_2
#define BUTTON3 SWITCH_3

int before = 1;

int buttonState = 3;
int lastButtonState = 3;

unsigned long lastDebounceTime = 0;
unsigned long debounceDelay = 50;

void setup() {
  // open serial port for communication with computer
  Serial.begin(9600);
}

void loop() {
  // read buttons
  int button1 = Esplora.readButton(BUTTON1);
  int button2 = Esplora.readButton(BUTTON2);
  int button3 = Esplora.readButton(BUTTON3);

  int reading = button1 + button2 + button3;

  if (reading != lastButtonState) {
    lastDebounceTime = millis();
  }

  // denounce
  if ((millis() - lastDebounceTime) > debounceDelay) {

    if (reading != buttonState) {
      buttonState = reading;

      // only one button is pressed
      if (buttonState == 2 && !before) {
        if (!button1) {
          Serial.print("GREEN\n");
          before = 1;
          Esplora.writeGreen(30);
        } else if (!button2) {
          Serial.print("RED\n");
          before = 1;
          Esplora.writeRed(30);
        } else {
          Serial.print("BLUE\n");
          before = 1;
          Esplora.writeBlue(30);
        }
      } else {
        before = 0;
        Esplora.writeGreen(0);
        Esplora.writeBlue(0);
        Esplora.writeRed(0);
      }
    }
  }

  lastButtonState = reading;
}
