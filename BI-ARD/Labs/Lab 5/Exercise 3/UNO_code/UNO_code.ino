// Arduino UNO

#include <SPI.h>
#include <SD.h>

#include <MCUFRIEND_kbv.h>
MCUFRIEND_kbv tft;
#include <TouchScreen.h>

// TFT display calibration
#define MINPRESSURE 100
#define MAXPRESSURE 1000

const int XP = 8, XM = A2, YP = A3, YM = 9; // 320x480 ID=0x9486
const int TS_LEFT = 127, TS_RT = 919, TS_TOP = 952, TS_BOT = 91;

TouchScreen ts = TouchScreen(XP, YP, XM, YM, 300);

// global variables
int secret_code[4];                                                // to hold the super secret password
int entered_code[4];                                               // to hold entered password
int pos = 0;                                                       // keeping track where in the entered password we are
int unlocker = 0;                                                  // once correct password is entered *** UNLOCKED *** is rendered
int pixel_x, pixel_y;                                              // where the display is touched
int last1 = 1, last2 = 1, last3 = 1, last4 = 1, last5 = 1, button; // debounce
int pushed;                                                        // what button was pressed
int nextStep = 1;                                                  // to render state on display

// colors
#define BLACK 0x0000
#define WHITE 0xFFFF
#define RED 0xF800
#define GREEN 0x07E0
#define BLUE 0x001F

// better for decoding states
enum states {
  LOCKED,
  LOCK_1,
  LOCK_2,
  LOCK_3,
  LOCK_4,
  UNLOCKED,
  WRONG
};
int STATE, NEXT_STATE;

void setup() {
  // Serial setup
  Serial.begin(9600);

  // display setup
  uint16_t ID = tft.readID();
  tft.begin(ID);
  tft.setRotation(0);
  tft.fillScreen(BLACK);

  // SD check
  if (!SD.begin(10)) {
    tft.print(F("No SD card inserted!"));
    while (1);
  }

  // file check
  if (!SD.exists("code.txt")) {
    tft.print(F("code.txt missing!"));
    while (1);
  }

  // get code
  File code;
  code = SD.open("code.txt");
  for (int i = 0; i < 4; i++) {
    secret_code[i] = (code.read() - 48);
  }
  code.close();

  // initial state
  tft.fillRect(0, 0, tft.width(), 61, GREEN);
  tft.setTextSize(3);
  tft.setCursor(17, 20);
  tft.println("*** UNLOCKED ***");

  // draw buttons
  tft.drawRect(120, 145, 80, 80, WHITE);
  tft.drawRect(40, 225, 80, 80, WHITE);
  tft.drawRect(200, 225, 80, 80, WHITE);
  tft.drawRect(120, 305, 80, 80, WHITE);

  tft.fillRect(80, 410, 160, 50, BLUE);

  tft.setTextSize(6);
  tft.setCursor(145, 164);
  tft.println("1");
  tft.setCursor(65, 244);
  tft.println("2");
  tft.setCursor(225, 244);
  tft.println("3");
  tft.setCursor(145, 324);
  tft.println("4");

  tft.setTextSize(3);
  tft.setCursor(125, 424);
  tft.println("LOCK");

  tft.setCursor(0, 0);

  STATE = UNLOCKED;
}

// checks if the entered password is correct - changes states
int passCheck() {
  for (int i = 0; i < 4; i++) {
    if (secret_code[i] != entered_code[i]) {
      NEXT_STATE = WRONG;
      return;
    }
  }
  NEXT_STATE = UNLOCKED;
  unlocker = 1;
  return;
}

// debounce reset
void holder() {
  last1 = 0;
  last2 = 0;
  last3 = 0;
  last4 = 0;
  last5 = 0;
  button = 0;
}

// runs the buttons
// if any was pressed then which one
// also uses the debounce so number is pressed only once
int buttons() {
  TSPoint p = ts.getPoint();
  pinMode(YP, OUTPUT);
  pinMode(XM, OUTPUT);
  digitalWrite(YP, HIGH);
  digitalWrite(XM, HIGH);
  bool pressed = (p.z > MINPRESSURE && p.z < MAXPRESSURE);

  pixel_x = map(p.x, TS_LEFT, TS_RT, 0, tft.width());
  pixel_y = map(p.y, TS_TOP, TS_BOT, 0, tft.height());

  if (pressed) {

    if (pixel_x >= 120 && pixel_x <= 200 && pixel_y >= 145 && pixel_y <= 225) {
      // pressed 1
      holder();
      pushed = 1;
    }
    else if (pixel_x >= 40 && pixel_x <= 120 && pixel_y >= 225 && pixel_y <= 305) {
      // pressed 2
      holder();
      pushed = 2;
    }
    else if (pixel_x >= 200 && pixel_x <= 280 && pixel_y >= 225 && pixel_y <= 305) {
      // pressed 3
      holder();
      pushed = 3;
    }
    else if (pixel_x >= 120 && pixel_x <= 200 && pixel_y >= 305 && pixel_y <= 385) {
      // pressed 4
      holder();
      pushed = 4;
    }
    else if (pixel_x >= 80 && pixel_x <= 240 && pixel_y >= 410 && pixel_y <= 460) {
      // lock pressed
      holder();
      pushed = 5;
    }
  } else {
    // debounce
    if (!last1) {
      last1 = 1;
    } else if (!last2) {
      last2 = 1;
    } else if (!last3) {
      last3 = 1;
    } else if (!last4) {
      last4 = 1;
    } else if (!last5) {
      last5 = 1;
    }

    if (last1 && last2 && last3 && last4 && last5)
      button = 1;
  }

  if (button && pushed) {
    // write entered number once no touch detected
    int tmp = pushed;
    pushed = 0;

    if (tmp != 5 && STATE != UNLOCKED)
      entered_code[pos++] = tmp;

    return tmp;
  }
  return 0;
}

// resets entered password
void passReset() {
  for (int i = 0; i < 4; i++)
    entered_code[i] = 0;

  pos = 0;
}

void loop() {

  int result = buttons();

  // STATE MACHINE
  switch (STATE) {
    case UNLOCKED:
      if (unlocker) {
        tft.setTextColor(WHITE);
        tft.fillRect(0, 0, tft.width(), 61, GREEN);
        tft.setTextSize(3);
        tft.setCursor(17, 20);
        tft.println("*** UNLOCKED ***");

        tft.setTextColor(BLACK);
        tft.setTextSize(5);
        tft.setCursor(57, 81);
        tft.println("* * * *");

        passReset();

        unlocker = 0;
      }

      if (result == 5) {
        NEXT_STATE = LOCKED;
        break;
      }

      NEXT_STATE = UNLOCKED;
      break;
    case LOCKED:
      if (nextStep) {
        tft.setTextColor(WHITE);
        tft.setTextSize(5);
        tft.setCursor(57, 81);
        tft.println("_ _ _ _");

        tft.fillRect(0, 0, tft.width(), 61, RED);
        tft.setTextSize(3);
        tft.setCursor(35, 20);
        tft.println("*** LOCKED ***");

        nextStep = 0;
      }

      if (result && result != 5) {
        NEXT_STATE = LOCK_1;
        nextStep = 1;
        break;
      }

      NEXT_STATE = LOCKED;
      break;
    case LOCK_1:
      if (nextStep) {
        tft.setTextColor(BLACK);
        tft.setTextSize(5);
        tft.setCursor(57, 81);
        tft.println("_");

        tft.setTextColor(WHITE);
        tft.setCursor(57, 81);
        tft.println("*");
        nextStep = 0;
      }

      if (result && result != 5) {
        NEXT_STATE = LOCK_2;
        nextStep = 1;
        break;
      }

      NEXT_STATE = LOCK_1;
      break;
    case LOCK_2:
      if (nextStep) {
        tft.setTextColor(BLACK);
        tft.setTextSize(5);
        tft.setCursor(57, 81);
        tft.println("_ _");

        tft.setTextColor(WHITE);
        tft.setCursor(57, 81);
        tft.println("* *");
        nextStep = 0;
      }

      if (result && result != 5) {
        NEXT_STATE = LOCK_3;
        nextStep = 1;
        break;
      }

      NEXT_STATE = LOCK_2;
      break;
    case LOCK_3:
      if (nextStep) {
        tft.setTextColor(BLACK);
        tft.setTextSize(5);
        tft.setCursor(57, 81);
        tft.println("_ _ _");

        tft.setTextColor(WHITE);
        tft.setCursor(57, 81);
        tft.println("* * *");
        nextStep = 0;
      }

      if (result && result != 5) {
        NEXT_STATE = LOCK_4;
        nextStep = 1;
        break;
      }

      NEXT_STATE = LOCK_3;
      break;
    case LOCK_4:
      if (nextStep) {
        tft.setTextColor(BLACK);
        tft.setTextSize(5);
        tft.setCursor(57, 81);
        tft.println("_ _ _ _");

        tft.setTextColor(WHITE);
        tft.setCursor(57, 81);
        tft.println("* * * *");
        nextStep = 0;
      }

      passCheck();
      nextStep = 1;

      break;
    case WRONG:
      tft.fillRect(0, 0, tft.width(), 61, RED);
      tft.setTextSize(3);
      tft.setCursor(44, 20);
      tft.println("*** WRONG ***");

      passReset();

      delay(2000);

      tft.setTextColor(BLACK);
      tft.setTextSize(5);
      tft.setCursor(57, 81);
      tft.println("* * * *");

      NEXT_STATE = LOCKED;

      break;
  }

  STATE = NEXT_STATE;
  
  // debugging info
  // for (int i = 0; i < 4; i++)
  //  Serial.print(entered_code[i]);
  // Serial.print(" ");
  // Serial.println(STATE);
}
