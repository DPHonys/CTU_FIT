// Arduino UNO

// pin definitions
#define YELLOW_PIN 11
#define GREEN_PIN 12
#define RED_PIN 13
#define INTERRUPT_PIN 2

// LED setup
volatile int currentLED = YELLOW_PIN;

void setup() {
  // Serial setup
  Serial.begin(9600);

  // Pin setup
  pinMode(GREEN_PIN, OUTPUT);
  pinMode(RED_PIN, OUTPUT);
  pinMode(YELLOW_PIN, OUTPUT);

  // interrupt setup
  pinMode(INTERRUPT_PIN, INPUT_PULLUP);
  attachInterrupt(digitalPinToInterrupt(INTERRUPT_PIN), changeLED, FALLING);
}

// changes blinking LED when interrupt occurred
void changeLED() {
  // debounce variables
  static unsigned long long int last = 0;
  unsigned long long int start = millis();

  // debounce
  if (start - last > 175) {
    // Turn off LED
    analogWrite(currentLED, LOW);

    // Then, choose a new LED
    if (currentLED == RED_PIN)
      currentLED = GREEN_PIN;

    else if (currentLED == GREEN_PIN)
      currentLED = YELLOW_PIN;

    else if (currentLED == YELLOW_PIN)
      currentLED = RED_PIN;

    // print current LED (PIN)
    Serial.print("PIN - ");
    Serial.println(currentLED);
  }

  last = start;
}

// Loop to blink a LED
void loop() {

  for (unsigned long long int start = millis(); (millis() - start) < 500;) {
    digitalWrite(currentLED, HIGH);
    delay(10);
  }

  for (unsigned long long int start = millis(); (millis() - start) < 500;) {
    digitalWrite(currentLED, LOW);
    delay(10);
  }
}
