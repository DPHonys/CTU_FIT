// Arduino ESPLORA

#include <Esplora.h>

int readJoyX = 0;
int readJoyY = 0;

int left, right, bottom, top, defaultX, defaultY;

void setup() {
  Serial.begin(9600);

  defaultX = Esplora.readJoystickX();
  defaultY = Esplora.readJoystickY();
}

void loop() {
  unsigned long long int start = millis();

  while ((millis() - start) <= 1000) {
    readJoyX = Esplora.readJoystickX();
    readJoyY = Esplora.readJoystickY();

    left = map(readJoyX, defaultX, 512, 0, 255);
    right = map(readJoyX, defaultX, -512, 0, 255);

    bottom = map(readJoyY, defaultY, 512, 0, 255);
    top = map(readJoyY, defaultY, -512, 0, 255);

    if (left < 0)
      left = 0;
    if (right < 0)
      right = 0;
    if (bottom < 0)
      bottom = 0;
    if (top < 0)
      top = 0;

    if (bottom == 0) {
      Esplora.writeRGB(top, left, right);
    } else {
      Esplora.writeRGB(bottom, left, right);
    }
  }

  Serial.print("X = ");
  Serial.print(readJoyX);
  Serial.print("\n");

  Serial.print("Y = ");
  Serial.print(readJoyY);
  Serial.print("\n");

  Serial.print("\t");
  Serial.print(top);
  Serial.print("\n");

  Serial.print(left);
  Serial.print("\t\t");
  Serial.print(right);
  Serial.print("\n");

  Serial.print("\t");
  Serial.print(bottom);
  Serial.print("\n\n");
}
