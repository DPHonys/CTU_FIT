// Arduino UNO

// Pin definitions
#define YELLOW_PIN 11
#define GREEN_PIN 12
#define RED_PIN 13

#define JoyX 0 // X
#define JoyY 1 // Y

void setup() {
  // Serial setup
  Serial.begin(9600);
}

// global variables
int incomingByte = 0;
int stateYellow = 0, stateRed = 0, stateGreen = 0;

void loop() {
  // If we have received data
  if (Serial.available() > 0) {
    incomingByte = Serial.read();

    if (incomingByte == 'G') { 
      // green LED
      if (!stateGreen) {
        // on
        digitalWrite(GREEN_PIN, HIGH);
        Serial.print('1');
        stateGreen = 1;
      } else {
        // off
        digitalWrite(GREEN_PIN, LOW);
        Serial.print('0');
        stateGreen = 0;
      }
    } else if (incomingByte == 'Y') { 
      // yellow led
      if (!stateYellow) {
        // on
        digitalWrite(YELLOW_PIN, HIGH);
        Serial.print('1');
        stateYellow = 1;
      } else {
        // off
        digitalWrite(YELLOW_PIN, LOW);
        Serial.print('0');
        stateYellow = 0;
      }
    } else if (incomingByte == 'R') {
      // red led
      if (!stateRed) {
        // on
        digitalWrite(RED_PIN, HIGH);
        Serial.print('1');
        stateRed = 1;
      } else {
        // off
        digitalWrite(RED_PIN, LOW);
        Serial.print('0');
        stateRed = 0;
      }
    } else if (incomingByte == 74) {
      // joystick status
      Serial.print(analogRead(JoyX));
      Serial.print(' ');
      Serial.print(analogRead(JoyY));
    }
  }
}
