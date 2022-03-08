// Arduino UNO

#define RED_LED 13
#define GREEN_LED 11
#define BLUE_LED 12

#define BUTTON_PIN 2         // pin on which to read a button press

#define DOT 200             // dot duration
#define DASH 800            // dash duration
#define NO_LIGHT 200        // no light duration

#define PAUSE 500           // pause duration between letters

// Debounce variables
int buttonState = LOW;
int lastButtonState = LOW;
unsigned long lastDebounceTime = 0;
unsigned long debounceDelay = 50;

// Morse code to display
// ARD : .- .-. -..

int currentLED = RED_LED;

// Change LED
void colorchange() {
  switch (currentLED) {
    case RED_LED:
      currentLED = BLUE_LED;
      break;
    case BLUE_LED:
      currentLED = GREEN_LED;
      break;
    case GREEN_LED:
      currentLED = RED_LED;
      break;
    default:
      break;
  }
}

void buttonpress(int duration, int state) {
  
  // Debounce button press
  unsigned long start = millis();

  while ( (millis() - start) <= duration ) {
    int reading = digitalRead(BUTTON_PIN);
    
    if (reading != lastButtonState) {
      lastDebounceTime = millis();
    }

    if ((millis() - lastDebounceTime) > debounceDelay) {

      if (reading != buttonState) {
        buttonState = reading;

        if (buttonState == HIGH) {
          if (state == 1) {
            digitalWrite(currentLED, LOW);
            colorchange();
            digitalWrite(currentLED, HIGH);
          } else {
          colorchange();
          }
        }
      }
    }

    lastButtonState = reading;
  }
}

// Flash the LED
void flash(int duration) {
  digitalWrite(currentLED, HIGH);
  buttonpress(duration, 1);
  digitalWrite(currentLED, LOW);
  buttonpress(NO_LIGHT, 0);
}

void setup() {
  // set pin modes
  pinMode(RED_LED, OUTPUT);
  pinMode(GREEN_LED, OUTPUT);
  pinMode(BLUE_LED, OUTPUT);
  pinMode(BUTTON_PIN, INPUT);
}

void loop() {
  flash(DOT);
  flash(DASH);
  buttonpress(200, 0);
  
  flash(DOT);
  flash(DASH);
  flash(DOT);
  buttonpress(200, 0);
  
  flash(DASH);
  flash(DOT);
  flash(DOT);
  buttonpress(500, 0);
}
