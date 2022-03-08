// Arduino UNO

#define red 3
#define white 5
#define green 9
#define yellow 6

#define JoyX 0
#define JoyY 1

int readJoyX = 0;
int readJoyY = 0;

int redLED, whiteLED, greenLED, yellowLED, defaultX, defaultY;

void setup() {
  Serial.begin(9600);

  defaultX = analogRead(JoyX);
  defaultY = analogRead(JoyY);
}

void loop() {
  unsigned long long int start = millis();

  while ( (millis() - start) <= 1000 ) {
    readJoyX = analogRead(JoyX);
    readJoyY = analogRead(JoyY);

    redLED = map(readJoyX, defaultX, 0, 0, 255);
    whiteLED = map(readJoyX, defaultX, 1023, 0, 255);

    greenLED = map(readJoyY, defaultY, 0, 0, 255);
    yellowLED = map(readJoyY, defaultY, 1023, 0, 255);

    if (redLED < 0)
      redLED = 0;
    if (whiteLED < 0)
      whiteLED = 0;
    if (greenLED < 0)
      greenLED = 0;     
    if (yellowLED < 0)
      yellowLED = 0;

    analogWrite(red, redLED);
    analogWrite(white, whiteLED);
    analogWrite(green, greenLED);
    analogWrite(yellow, yellowLED);
  }

  Serial.print("X = ");
  Serial.print(readJoyX);
  Serial.print("\n");

  Serial.print("Y = ");
  Serial.print(readJoyY);
  Serial.print("\n");

  Serial.print("\t");
  Serial.print(redLED);
  Serial.print("\n");

  Serial.print(yellowLED);
  Serial.print("\t\t");
  Serial.print(greenLED);
  Serial.print("\n");

  Serial.print("\t");
  Serial.print(whiteLED);
  Serial.print("\n\n");
}
