// Arduino ESPLORA

#include <Esplora.h>

#define UP_TRESHOLD -50    // zde vhodně nastavte hranici, která určí, že je joystick v pozici nahoře
#define DOWN_TRESHOLD 50   // zde vhodně nastavte hranici, která určí, že je joystick v pozici dole
#define RIGHT_TRESHOLD -50 // zde vhodně nastavte hranici, která určí, že je joystick v pozici vpravo
#define LEFT_TRESHOLD 50   // zde vhodně nastavte hranici, která určí, že je joystick v pozici vlevo

#define CENTER 0
#define LEFT 1
#define UP 2
#define RIGHT 3
#define DOWN 4
#define PUSH 5

int reset = 0;
int button, xPos, yPos, slider, slidermapped, pos_joystick;

void timerFix() {
#define sbi(sfr, b) (_SFR_BYTE(sfr) |= _BV(b))

  // Tone will have hijacked the timer used for the
  // RGB led RED channel so once we're done we need
  // to restore it.  First shutdown the tone internals
  // if not done already...
  Esplora.noTone();

  // Now clear the Timer Count Control Registers to
  // have them in a known state.
  TCCR3A = 0;
  TCCR3B = 0;

  // Setup the clock source - clk/64
  sbi(TCCR3B, CS31);
  sbi(TCCR3B, CS30);

  // Set the wave form generator for 10-bit PWM
  sbi(TCCR3A, WGM30);

  // re-link the PWM timer to output channel
  // by passing something other than 0 and 255
  // so that the analogWrite function is forced to
  // recompute the correct value for either the
  // OCR3A or OCR3B register (output control register)
  // as appropriate
  analogWrite(5, 1);

  // turn the LED channel off
  analogWrite(5, 0);
}

int readJoystick() {
  button = Esplora.readJoystickButton();

  if (button == LOW)
  {
    reset = 1;
    return PUSH;
  }

  if (button == HIGH && reset)
  {
    timerFix();
    reset = 0;
  }

  xPos = Esplora.readJoystickX();
  yPos = Esplora.readJoystickY();

  if (yPos < UP_TRESHOLD) { // joystick is up
    return UP;
  }

  if (yPos > DOWN_TRESHOLD) { // joystick is down
    return DOWN;
  }

  if (xPos < RIGHT_TRESHOLD) { // joystick is right
    return RIGHT;
  }

  if (xPos > LEFT_TRESHOLD) { // joystick is left
    return LEFT;
  }

  return CENTER;
}

void setup() {
  Serial.begin(9600);
}

void loop() {
  unsigned long long int start = millis();

  while ((millis() - start) <= 1000) {

    slider = Esplora.readSlider();
    slidermapped = map(slider, 0, 1023, 0, 255);

    pos_joystick = readJoystick();

    switch (pos_joystick) {
      case LEFT:
        // RED
        Esplora.writeRGB(slidermapped / 2, 0, 0);
        break;
      case RIGHT:
        // GREEN
        Esplora.writeRGB(0, slidermapped / 2, 0);
        break;
      case UP:
        // BLUE
        Esplora.writeRGB(0, 0, slidermapped / 2);
        break;
      case DOWN:
        // WHITE
        Esplora.writeRGB(slidermapped / 2, slidermapped / 2, slidermapped / 2);
        break;
      case CENTER:
        // OFF
        Esplora.writeRGB(0, 0, 0);
        break;
      case PUSH:
        // TONE
        Esplora.tone(slidermapped);
        break;
    }
  }

  Serial.print("SLIDER : ");
  Serial.print(slider);
  Serial.print(" -> ");
  Serial.print(slidermapped);
  Serial.print("\n");

  Serial.print("STATUS : ");
  switch (pos_joystick) {
    case LEFT:
      Serial.print("LEFT");
      break;
    case RIGHT:
      Serial.print("RIGHT");
      break;
    case UP:
      Serial.print("UP");
      break;
    case DOWN:
      Serial.print("DOWN");
      break;
    case CENTER:
      Serial.print("CENTER");
      break;
    case PUSH:
      Serial.print("PUSH");
      break;
  }
  Serial.print("\n\n");
}
