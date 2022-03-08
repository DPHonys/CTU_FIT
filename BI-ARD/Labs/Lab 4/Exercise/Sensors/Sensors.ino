// Arduino ESPLORA

#include <TFT.h> // Hardware-specific library
#include <SPI.h>
#include <Esplora.h>

#define DOWN SWITCH_1
#define UP SWITCH_4
#define ENTER SWITCH_2
#define BACK SWITCH_3

byte buttonFlag = 0;

bool buttonEvent(int button) {
  switch (button) {
    case UP:
      if (Esplora.readButton(UP) == LOW) {
        buttonFlag |= 1;
      } else if (buttonFlag & 1) {
        buttonFlag ^= 1;
        return true;
      }
      break;

    case DOWN:
      if (Esplora.readButton(DOWN) == LOW) {
        buttonFlag |= 2;
      } else if (buttonFlag & 2) {
        buttonFlag ^= 2;
        return true;
      }
      break;

    case BACK:
      if (Esplora.readButton(BACK) == LOW) {
        buttonFlag |= 4;
      } else if (buttonFlag & 4) {
        buttonFlag ^= 4;
        return true;
      }
      break;

    case ENTER:
      if (Esplora.readButton(ENTER) == LOW) {
        buttonFlag |= 8;
      } else if (buttonFlag & 8) {
        buttonFlag ^= 8;
        return true;
      }
  }

  return false;
}

int pos = 30, displayFirst = 0, subInit = 1;

int postinion = 0;

enum states {
  HOME_SEL_TEMP,
  HOME_SEL_MIC,
  HOME_SEL_ACCE,
  TEMP,
  MIC,
  ACCE
};
enum states STATE, NEXT_STATE;

// main menu
void display_menu() {
  EsploraTFT.setTextSize(1);
  EsploraTFT.background(0, 0, 0); // clear the screen
  EsploraTFT.stroke(255, 255, 255);

  EsploraTFT.text("Select an option:", 10, 10);
  EsploraTFT.text("1. Temperature", 20, 30);
  EsploraTFT.text("2. Microphone", 20, 40);
  EsploraTFT.text("3. Akcelerometer", 20, 50);

  if (displayFirst) {
    EsploraTFT.stroke(255, 255, 255);
    EsploraTFT.text(">", 10, pos);
  }

  displayFirst = 1;
}

// change position of the cursor
void change_position(int x) {
  if (x == 1) {
    // cursor down
    EsploraTFT.stroke(0, 0, 0);
    EsploraTFT.text(">", 10, pos);
    pos += 10;
    EsploraTFT.stroke(255, 255, 255);
    EsploraTFT.text(">", 10, pos);
  } else {
    // cursor up
    EsploraTFT.stroke(0, 0, 0);
    EsploraTFT.text(">", 10, pos);
    pos -= 10;
    EsploraTFT.stroke(255, 255, 255);
    EsploraTFT.text(">", 10, pos);
  }
}

// go back to main menu
int backHome(int last) {
  if (buttonEvent(BACK)) {
    subInit = 1;
    display_menu();

    switch (last) {
      case TEMP:
        NEXT_STATE = HOME_SEL_TEMP;
        break;
      case MIC:
        NEXT_STATE = HOME_SEL_MIC;
        break;
      case ACCE:
        NEXT_STATE = HOME_SEL_ACCE;
        break;
    }

    return 1;
  }
  return 0;
}

void setup() {
  // Display initialization
  EsploraTFT.begin();
  Serial.begin(9600);
  display_menu(); // show menu
  EsploraTFT.text(">", 10, pos);
  STATE = HOME_SEL_TEMP;
}

// TEMPERATURE APP
void draw_temp() {
  int temp;
  char Ctemp[3];

  int next;
  char Cnext[3];

  // initialization
  if (subInit) {
    EsploraTFT.background(0, 0, 0);

    EsploraTFT.stroke(255, 255, 255);
    EsploraTFT.text("TEMPERATURE", 10, 10);

    // first temp read
    temp = Esplora.readTemperature(DEGREES_C);
    String Stemp = String(temp);
    Stemp.toCharArray(Ctemp, 3);

    EsploraTFT.setTextSize(3);
    EsploraTFT.text(Ctemp, 10, 30);
    EsploraTFT.setTextSize(2);
    EsploraTFT.text("C", 50, 37);
    EsploraTFT.setTextSize(3);

    subInit = 0;
  }

  // MILLIS
  unsigned long long int start = millis();

  while ((millis() - start) <= 2000) {
    // back to menu
    if (backHome(TEMP)) {
      return;
    }
    // check temperature
    next = Esplora.readTemperature(DEGREES_C);
    String Snext = String(next);
    Snext.toCharArray(Cnext, 3);
  }

  // update displayed temp if changed
  if (strcmp(Ctemp, Cnext)) {
    // remove old
    EsploraTFT.stroke(0, 0, 0);
    EsploraTFT.text(Ctemp, 10, 30);

    // display new
    EsploraTFT.stroke(255, 255, 255);
    EsploraTFT.text(Cnext, 10, 30);

    strcpy(Ctemp, Cnext);
  }
}

const int MAX_W = EsploraTFT.width();
const int MAX_H = EsploraTFT.height();

int xPos = 0;
int yPos = 0;
int xPrev = 0;
int yPrev = MAX_H;
char printout[5];
int def;

// MICROPHONE APP
void draw_mic() {
  // initialization
  if (subInit) {
    EsploraTFT.background(0, 0, 0);
    def = Esplora.readMicrophone();
    EsploraTFT.stroke(255, 0, 0);

    subInit = 0;
  }

  unsigned long long int start = millis();

  while ((millis() - start) <= 50) {
    // back to menu
    if (backHome(MIC)) {
      EsploraTFT.stroke(255, 255, 255);
      xPos = 0;
      yPos = 0;
      xPrev = 0;
      return;
    }
  }

  int mic = Esplora.readMicrophone();
  int micMapped = map(mic, def, 1023, (MAX_H - 5), 0);

  // moving on x axis
  xPos++;

  // draw a line on y axis
  yPos = micMapped;
  EsploraTFT.line(xPrev, yPrev, xPos, yPos);

  // reset after reaching end of screen
  if (xPos >= MAX_W) {
    EsploraTFT.background(0, 0, 0);
    xPos = 0;
  }

  xPrev = xPos;
  yPrev = yPos;
}

int oldxA, oldyA, xAxis, yAxis, xDef, yDef, oldX, oldY, newX, newY, redraw;

// ACCELERATION APP
void draw_acce() {
  // initialization
  if (subInit) {
    EsploraTFT.background(0, 0, 0);

    // start with cicrle in the center
    EsploraTFT.stroke(255, 255, 255);
    EsploraTFT.fill(255, 255, 255);
    EsploraTFT.circle(EsploraTFT.width() / 2, EsploraTFT.height() / 2, 10);
    oldX = newX = EsploraTFT.width() / 2;
    oldY = newY = EsploraTFT.height() / 2;

    redraw = 0;

    xDef = Esplora.readAccelerometer(X_AXIS);
    yDef = Esplora.readAccelerometer(Y_AXIS);

    subInit = 0;
  }

  // back to menu
  if (backHome(ACCE)) {
    return;
  }

  xAxis = Esplora.readAccelerometer(X_AXIS);
  yAxis = Esplora.readAccelerometer(Y_AXIS);

  if (xAxis > (xDef + 30) && (newX != 10)) {
    // going left
    delay(5);
    newX -= 1;
    redraw = 1;
  } else if (xAxis < (xDef - 30) && (newX != (MAX_W - 10))) {
    // going right
    delay(5);
    newX += 1;
    redraw = 1;
  }

  if (yAxis < (yDef - 30) && (newY != 10)) {
    // going down
    delay(5);
    newY -= 1;
    redraw = 1;
  } else if (yAxis > (yDef + 30) && (newY != (MAX_H - 10))) {
    // going up
    delay(5);
    newY += 1;
    redraw = 1;
  }

  // redraw circle if needed
  if (redraw) {
    // remove old
    EsploraTFT.stroke(0, 0, 0);
    EsploraTFT.fill(0, 0, 0);
    EsploraTFT.circle(oldX, oldY, 10);

    // draw new
    EsploraTFT.stroke(255, 255, 255);
    EsploraTFT.fill(255, 255, 255);
    EsploraTFT.circle(newX, newY, 10);

    redraw = 0;
    oldX = newX;
    oldY = newY;
  }
}

// STATE MACHINE LOOP
void loop() {
  switch (STATE) {
    // MENU - TEMPERATURE
    case HOME_SEL_TEMP:
      if (buttonEvent(DOWN)) {
        // pressed down button
        change_position(1);
        NEXT_STATE = HOME_SEL_MIC;
      } else if (buttonEvent(ENTER)) {
        // pressed enter button
        NEXT_STATE = TEMP;
      }
      break;

    // MENU - MICROPHONE
    case HOME_SEL_MIC:
      if (buttonEvent(DOWN)) {
        // pressed down button
        change_position(1);
        NEXT_STATE = HOME_SEL_ACCE;
      } else if (buttonEvent(UP)) {
        // pressed up button
        change_position(2);
        NEXT_STATE = HOME_SEL_TEMP;
      } else if (buttonEvent(ENTER)) {
        // pressed enter button
        NEXT_STATE = MIC;
      }
      break;

    // MENU - ACCELEROMETER
    case HOME_SEL_ACCE:
      if (buttonEvent(UP)) {
        // pressed up button
        change_position(2);
        NEXT_STATE = HOME_SEL_MIC;
      } else if (buttonEvent(ENTER)) {
        // pressed enter button
        NEXT_STATE = ACCE;
      }
      break;

    // DRAW TEMPERATURE
    case TEMP:
      if (buttonEvent(BACK)) {
        subInit = 1;
        display_menu();
        NEXT_STATE = HOME_SEL_TEMP;
      } else {
        draw_temp();
      }
      break;

    // DRAW MICROPHONE
    case MIC:
      if (buttonEvent(BACK)) {
        subInit = 1;
        display_menu();
        NEXT_STATE = HOME_SEL_MIC;
      } else {
        draw_mic();
      }
      break;

    // DRAW ACCELEROMETER
    case ACCE:
      if (buttonEvent(BACK)) {
        subInit = 1;
        display_menu();
        NEXT_STATE = HOME_SEL_ACCE;
      } else {
        draw_acce();
      }
      break;
  }

  STATE = NEXT_STATE;
}
