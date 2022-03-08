// Arduino ESPLORA

#include <Esplora.h>

enum states {
  STATE_START,
  STATE_SW1,
  STATE_SW2,
  STATE_SW1SW2
};

enum states STATE, NEXT_STATE;

void setup() {
  Serial.begin(9600);
}

bool sw1_pressed = false, sw2_pressed = false;

void loop() {
  switch (STATE) {
    case STATE_START:
      Esplora.writeRGB(0, 0, 0);
      sw1_pressed = !(Esplora.readButton(SWITCH_1));
      sw2_pressed = !(Esplora.readButton(SWITCH_2));

      if (sw1_pressed == true) {
        NEXT_STATE = STATE_SW1;
      } else if (sw2_pressed == true) {
        NEXT_STATE = STATE_SW2;
      }

      break;

    case STATE_SW1:
      sw1_pressed = !(Esplora.readButton(SWITCH_1));
      sw2_pressed = !(Esplora.readButton(SWITCH_2));
      if (sw1_pressed == false) {
        Serial.print("STATE_A\n");
        Esplora.writeRGB(155, 0, 0);
        delay(100);
        NEXT_STATE = STATE_START;
        break;
      } else if (sw2_pressed == true) {
        NEXT_STATE = STATE_SW1SW2;
        break;
      }

      break;

    case STATE_SW2:
      sw1_pressed = !(Esplora.readButton(SWITCH_1));
      sw2_pressed = !(Esplora.readButton(SWITCH_2));
      if (sw2_pressed == false) {
        Serial.print("STATE_B\n");
        Esplora.writeRGB(0, 155, 0);
        delay(100);
        NEXT_STATE = STATE_START;
        break;
      } else if (sw1_pressed == true) {
        NEXT_STATE = STATE_SW1SW2;
        break;
      }

      break;

    case STATE_SW1SW2:
      sw1_pressed = !(Esplora.readButton(SWITCH_1));
      sw2_pressed = !(Esplora.readButton(SWITCH_2));
      if (sw2_pressed == false && sw1_pressed == false) {
        Serial.print("STATE_C\n");
        Esplora.writeRGB(0, 0, 155);
        delay(100);
        NEXT_STATE = STATE_START;
        break;
      }
      break;
  }

  STATE = NEXT_STATE;
}
