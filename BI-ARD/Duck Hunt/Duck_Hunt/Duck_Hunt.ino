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

//------------------------------------------------------------------------------

// COLORS
#define BLACK 0x0000
#define WHITE 0xFFFF
#define RED 0xF800
#define GOLD 0xFDE0
#define mainBLUE 0x5E7D
#define GREEN1 0x6E09
#define GREEN2 0x4CC8
#define GREEN3 0x1362
#define BROWN1 0x7A26
#define BROWN2 0x6183
#define BROWN3 0x4944
#define NEON 0xA7C5
#define duckGREY 0xF79E
#define duckBROWN1 0x30A2
#define duckBROWN2 0x5104
#define duckBROWN3 0x69C5
#define duckORANGE1 0xF3C8
#define duckORANGE2 0xFD2A
#define duckGREEN1 0x12E4
#define duckGREEN2 0x2DC6

//------------------------------------------------------------------------------

// GLOBAL VARIABLES
int last1 = 1, last2 = 1, last3 = 1, last4 = 1, last5 = 1, button = 1; // debounce variables
int touchX = 0, touchY = 0, X = 0, Y = 0;                              // variables for touch
int initState = 0;
int start = 3;
int bulletsCount;
int duckStartX, duckCurX, duckCurY, moves;           // duck variables
int finnalScore, killTime, timeMapped;               // score variables
int newDIRECTION;                                    // edge variable
int roundCount;                                      // number of rounds
unsigned long long int roundStart, roundEnd = 30000; // keeps track of round length
unsigned long long int touchSTART, touchEND;         // time for player to kill the duck
int disableEDGE;

//------------------------------------------------------------------------------

// FSM STATES
// MAIN LOOP STATES
enum states {
    MENU,
    CREDITS,
    START,
    ROUND,
    END
};
enum states STATE, NEXT_STATE;
// DUCK STATES
enum duckState {
    upLtR,
    upRtL,
    lineLtR,
    lineRtL,
    DEAD,
    downLtR,
    downRtL,
    flyAWAY,
    DONE
};
enum duckState curDuckState, nextDuckState;

//------------------------------------------------------------------------------

// DRAWERS
// Prints a credits onto a screen
void creditsScreen() {
    tft.fillRect(20, 20, (tft.width() - 40), 180, NEON);
    tft.fillRect(23, 23, (tft.width() - 46), 174, BLACK);

    tft.fillRect(150, 226, 180, 60, BLACK);

    tft.setTextSize(4);
    tft.setCursor(194, 242);
    tft.println(F("BACK"));

    tft.setTextSize(3);
    tft.setCursor(((tft.width() / 2) - 70), 43);
    tft.println(F("< CODE >"));

    tft.setTextSize(2);
    tft.setCursor(((tft.width() / 2) - 71), 79);
    tft.println(F("Daniel Honys"));

    tft.setTextSize(1);
    tft.setCursor(((tft.width() / 2) - 59), 98);
    tft.println(F("honysdan@fit.cvut.cz"));
}

// Prints end screen after run with score
void endScreen() {
    tft.fillRect(20, 20, (tft.width() - 40), 180, NEON);
    tft.fillRect(23, 23, (tft.width() - 46), 174, BLACK);

    tft.fillRect(150, 226, 180, 60, BLACK);

    tft.setTextSize(4);
    tft.setCursor(194, 242);
    tft.println(F("BACK"));

    tft.setTextSize(2);
    tft.setCursor(((tft.width() / 2) - 65), 43);
    tft.println(F("!!! END !!!"));

    tft.setTextSize(5);
    tft.setCursor(((tft.width() / 2) - 72), 77);
    tft.println(F("SCORE"));

    tft.setTextSize(3);

    if (finnalScore == 0) {
        tft.setCursor(((tft.width() / 2) - 7), 132);
        tft.println(finnalScore);
    } else if (finnalScore >= 100 && finnalScore < 1000) {
        tft.setCursor(((tft.width() / 2) - 25), 132);
        tft.println(finnalScore);
    } else if (finnalScore >= 1000 && finnalScore < 10000) {
        tft.setCursor(((tft.width() / 2) - 34), 132);
        tft.println(finnalScore);
    } else {
        tft.setCursor(((tft.width() / 2) - 43), 132);
        tft.println(finnalScore);
    }
}

// Paints the sky
void sky() {
    tft.fillRect(0, 0, tft.width(), 215, mainBLUE);
}

// Paints the ground
void ground() {
    tft.fillRect(0, 215, tft.width(), 3, GREEN1);
    tft.fillRect(0, 218, tft.width(), 24, GREEN2);
    tft.fillRect(0, 242, tft.width(), 3, GREEN3);

    tft.fillRect(0, 245, tft.width(), 48, BROWN1);
    tft.fillRect(0, 293, tft.width(), 12, BROWN2);
    tft.fillRect(0, 305, tft.width(), 15, BROWN3);
}

// Paints the whole scene
void background() {
    sky();
    ground();
}

// Paints the main menu
void menu() {
    tft.setTextSize(8);

    tft.setCursor(32, 68);
    tft.setTextColor(BLACK);
    tft.println(F("DUCK HUNT"));

    tft.setCursor(31, 67);
    tft.setTextColor(BLACK);
    tft.println(F("DUCK HUNT"));

    tft.setCursor(30, 66);
    tft.setTextColor(BLACK);
    tft.println(F("DUCK HUNT"));

    tft.setCursor(29, 65);
    tft.setTextColor(BLACK);
    tft.println(F("DUCK HUNT"));

    tft.setCursor(28, 64);
    tft.setTextColor(WHITE);
    tft.println(F("DUCK HUNT"));

    tft.fillRect(147, 147, 186, 66, NEON);
    tft.fillRect(150, 150, 180, 60, BLACK);

    tft.fillRect(150, 226, 180, 60, BLACK);

    tft.setTextSize(4);
    tft.setCursor(182, 166);
    tft.println(F("START"));
    tft.setCursor(158, 242);
    tft.println(F("CREDITS"));
}

//------------------------------------------------------------------------------

// BUTTON CHECKERS
// checks if start button is pressed
int startButton() {
    if ((touchX >= 147) && (touchX <= 330) && (touchY >= 150) && (touchY <= 210)) {
        return 1;
    }
    return 0;
}

// checks if creditsScreen/back/menu button is pressed
int multiButton() {
    if ((touchX >= 147) && (touchX <= 330) && (touchY >= 230) && (touchY <= 285)) {
        return 1;
    }
    return 0;
}

//------------------------------------------------------------------------------

// TOUCH HANDELING
// defaults the touch so its ignored
void nullTouch() {
    touchX = 0;
    touchY = 0;
}

// touch checker / touch locker / tocuh debouncer
void touch() {
    TSPoint p = ts.getPoint();
    pinMode(YP, OUTPUT);
    pinMode(XM, OUTPUT);
    digitalWrite(YP, HIGH);
    digitalWrite(XM, HIGH);
    bool pressed = (p.z > MINPRESSURE && p.z < MAXPRESSURE);

    X = map(p.y, TS_TOP, TS_BOT, 0, 480);
    Y = map(p.x, TS_RT, TS_LEFT, 0, 320);

    if (pressed) {
        if (button) {
            touchY = Y;
            touchX = X;
        }
        button = 0;
        last1 = 0;
        last2 = 0;
        last3 = 0;
        last4 = 0;
        last5 = 0;
    } else {
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

        if (last1 && last2 && last3 && last4 && last5) {
            button = 1;
            nullTouch();
        }
    }
}

//------------------------------------------------------------------------------

// START COUNTER
void startCount() {
    unsigned long long int abc = millis();

    String num = String(start);
    tft.setCursor(((tft.width() / 2) - 25), 100);
    tft.println(num);

    while (millis() - abc <= 500) {}
}

//------------------------------------------------------------------------------

// BULLETS
// draws a bullet display
void bulletDisplay() {
    tft.fillRect(20, 233, 108, 66, NEON);
    tft.fillRect(23, 236, 102, 60, BLACK);

    tft.setTextSize(2);

    tft.setCursor(33, 246);
    tft.println(F("BULLETS"));
    bullets();
}

// draws a bullet
void printBullet(int x, int y) {
    tft.fillRect(x, y, 10, 20, RED);
    tft.fillRect(x - 2, y + 20, 14, 2, GOLD);
}

// deletes a bullet
void deleteBullet(int x, int y) {
    tft.fillRect(x - 2, y, 14, 22, BLACK);
}

// operates bullet display
void bullets() {
    switch (bulletsCount) {
        case 3:
            printBullet(45, 265);
            printBullet(69, 265);
            printBullet(93, 265);
            break;
        case 2:
            deleteBullet(93, 265);
            break;
        case 1:
            deleteBullet(69, 265);
            break;
        case 0:
            deleteBullet(45, 265);
            break;
    }
}

//------------------------------------------------------------------------------

// DUCK HANDELING
// sets values for duck to start on
void initDuck() {
    duckStartX = rand() % 93;

    if (duckStartX < 2) {
        duckStartX = 2;
    } else if (duckStartX > 39 && duckStartX < 48) {
        duckStartX = 39;
    } else if (duckStartX >= 48 && duckStartX < 57) {
        duckStartX = 57;
    }

    moves = (rand() % 28) + 10;

    duckCurX = duckStartX * 5;
    duckCurY = 185;

    if (duckStartX < 48) {
        duckUpLtR(duckCurX, duckCurY);
        curDuckState = nextDuckState = upLtR;
    } else {
        duckUpRtL(duckCurX, duckCurY);
        curDuckState = nextDuckState = upRtL;
    }
}

// FSM for duck
void duck() {
    switch (curDuckState) {
        case upLtR:
            duckCurX = duckCurX + 5;
            duckCurY = duckCurY - 5;
            moves--;
            duckUpLtR(duckCurX, duckCurY);
            if (moves == 0) {
                nextDuckState = lineLtR;
            }
            break;
        case upRtL:
            duckCurX = duckCurX - 5;
            duckCurY = duckCurY - 5;
            moves--;
            duckUpRtL(duckCurX, duckCurY);
            if (moves == 0) {
                nextDuckState = lineRtL;
            }
            break;
        case lineLtR:
            duckCurX = duckCurX + 5;
            duckFlyLtR(duckCurX, duckCurY);
            break;
        case lineRtL:
            duckCurX = duckCurX - 5;
            duckFlyRtL(duckCurX, duckCurY);
            break;
        case downLtR:
            duckCurX = duckCurX + 5;
            duckCurY = duckCurY + 5;
            moves--;
            duckDownLtR(duckCurX, duckCurY);
            if (moves == 0) {
                duckCurY = duckCurY + 5;
                sky();
                nextDuckState = lineLtR;
            }
            break;
        case downRtL:
            duckCurX = duckCurX - 5;
            duckCurY = duckCurY + 5;
            moves--;
            duckDownRtL(duckCurX, duckCurY);
            if (moves == 0) {
                duckCurY = duckCurY + 5;
                sky();
                nextDuckState = lineRtL;
            }
            break;
        case DEAD:
            if (duckCurY >= 170) {
                nextDuckState = DONE;
                break;
            }
            duckCurY = duckCurY + 5;
            duckDrop(duckCurX, duckCurY);
            break;
        case flyAWAY:
            if (duckCurX <= -45 || duckCurX >= 480) {
                nextDuckState = DONE;
                break;
            }
            if (duckCurX < tft.width() / 2) {
                duckCurX = duckCurX - 5;
                duckFlyRtL(duckCurX, duckCurY);
            } else {
                duckCurX = duckCurX + 5;
                duckFlyLtR(duckCurX, duckCurY);
            }
            break;
        case DONE:
            break;
    }
    curDuckState = nextDuckState;
}

// detects if duck reached any edge
void edgeDetect() {
    newDIRECTION = rand() % 3;
    if (duckCurX >= tft.width() - 45) {
        switch (newDIRECTION) {
            case 0:
                curDuckState = nextDuckState = lineRtL;
                break;
            case 1:
                moves = rand() % 10;
                curDuckState = nextDuckState = upRtL;
                break;
            case 2:
                moves = rand() % 10;
                curDuckState = nextDuckState = downRtL;
                break;
        }
        Serial.println(F("EDGE"));
        sky();
    } else if (duckCurX <= 0) {
        switch (newDIRECTION) {
            case 0:
                curDuckState = nextDuckState = lineLtR;
                break;
            case 1:
                moves = rand() % 10;
                curDuckState = nextDuckState = upLtR;
                break;
            case 2:
                moves = rand() % 10;
                curDuckState = nextDuckState = downLtR;
                break;
        }
        Serial.println(F("EDGE"));
        sky();
    } else if (duckCurY >= 185) {
        newDIRECTION = rand() % 2;
        switch (newDIRECTION) {
            case 0:
                moves = rand() % 25;
                curDuckState = nextDuckState = upRtL;
                break;
            case 1:
                moves = rand() % 25;
                curDuckState = nextDuckState = upLtR;
                break;
        }
        Serial.println(F("EDGE"));
        sky();
    } else if (duckCurY <= 0) {
        newDIRECTION = rand() % 2;
        switch (newDIRECTION) {
            case 0:
                moves = rand() % 10;
                curDuckState = nextDuckState = downLtR;
                break;
            case 1:
                moves = rand() % 10;
                curDuckState = nextDuckState = downRtL;
                break;
        }
        Serial.println(F("EDGE"));
        sky();
    }
}

// checks if duck was killed
int kill() {
    if ((touchX >= (duckCurX - 5)) && (touchX <= (duckCurX + 50)) && (touchY >= (duckCurY - 5)) && (touchY <= (duckCurY + 30))) {
        return 1;
    }
    return 0;
}

// checks if duck was mised
int miss() {
    if (touchX >= 0 && touchX <= tft.width() && touchY >= 0 && touchY < 215) {
        return 1;
    }
    return 0;
}

//------------------------------------------------------------------------------

// calculates the score of a run
void score() {
    if (killTime >= 10000) {
        killTime = 10000;
    }

    timeMapped = map(killTime, 0, 10000, 100, 0);

    finnalScore = finnalScore + 500 + (((float)timeMapped / 100) * 4500);
}

//------------------------------------------------------------------------------

// DUCK RENDER
// DUCK from left to right
void duckDownLtR(int x, int y) {
    tft.fillRect(x, y, 45, 30, mainBLUE);
    printDuckLtR(x + 5, y + 5);
}

void duckUpLtR(int x, int y) {
    tft.fillRect(x, y, 45, 30, mainBLUE);
    printDuckLtR(x + 5, y);
}

void duckFlyLtR(int x, int y) {
    tft.fillRect(x, y, 45, 25, mainBLUE);
    printDuckLtR(x + 5, y);
}

void duckStillLtR(int x, int y) {
    tft.fillRect(x, y, 40, 25, mainBLUE);
    printDuckLtR(x, y);
}
// DRAWS THE DUCK
void printDuckLtR(int x, int y) {
    tft.fillRect(x + 10, y + 7, 20, 15, duckGREY);
    tft.fillRect(x + 10, y + 7, 10, 15, duckBROWN1);
    tft.fillRect(x + 20, y + 7, 5, 10, duckBROWN1);
    tft.fillRect(x + 5, y + 2, 15, 10, duckBROWN2);
    tft.fillRect(x, y + 2, 15, 5, duckBROWN3);

    tft.fillRect(x + 12, y + 22, 3, 3, duckORANGE1);
    tft.fillRect(x + 17, y + 22, 3, 3, duckORANGE1);

    tft.fillRect(x + 35, y + 7, 5, 5, duckORANGE1);
    tft.fillRect(x + 35, y + 7, 5, 2, duckORANGE2);

    tft.fillRect(x + 25, y, 10, 12, duckGREEN1);
    tft.fillRect(x + 25, y, 10, 5, duckGREEN2);

    tft.fillRect(x + 30, y + 2, 5, 5, WHITE);
    tft.fillRect(x + 33, y + 4, 2, 3, BLACK);
}

// DUCK from right to left
void duckDownRtL(int x, int y) {
    tft.fillRect(x, y, 45, 30, mainBLUE);
    printDuckRtL(x, y + 5);
}

void duckUpRtL(int x, int y) {
    tft.fillRect(x, y, 45, 30, mainBLUE);
    printDuckRtL(x, y);
}

void duckFlyRtL(int x, int y) {
    tft.fillRect(x, y, 45, 25, mainBLUE);
    printDuckRtL(x, y);
}

void duckStillRtL(int x, int y) {
    tft.fillRect(x, y, 40, 25, mainBLUE);
    printDuckRtL(x, y);
}
// DRAWS THE DUCK
void printDuckRtL(int x, int y) {
    tft.fillRect(x + 10, y + 7, 20, 15, duckGREY);
    tft.fillRect(x + 20, y + 7, 10, 15, duckBROWN1);
    tft.fillRect(x + 15, y + 7, 5, 10, duckBROWN1);
    tft.fillRect(x + 20, y + 2, 15, 10, duckBROWN2);
    tft.fillRect(x + 25, y + 2, 15, 5, duckBROWN3);

    tft.fillRect(x + 20, y + 22, 3, 3, duckORANGE1);
    tft.fillRect(x + 25, y + 22, 3, 3, duckORANGE1);

    tft.fillRect(x, y + 7, 5, 5, duckORANGE1);
    tft.fillRect(x, y + 7, 5, 2, duckORANGE2);

    tft.fillRect(x + 5, y, 10, 12, duckGREEN1);
    tft.fillRect(x + 5, y, 10, 5, duckGREEN2);

    tft.fillRect(x + 5, y + 2, 5, 5, WHITE);
    tft.fillRect(x + 4, y + 4, 2, 3, BLACK);
}

// DEAD DUCK
void duckDrop(int x, int y) {
    tft.fillRect(x, y, 25, 45, mainBLUE);
    printDrop(x, y + 5);
}
// DRAWS THE DUCK
void printDrop(int x, int y) {
    tft.fillRect(x + 7, y + 10, 15, 20, duckGREY);
    tft.fillRect(x + 7, y + 10, 15, 10, duckBROWN1);
    tft.fillRect(x + 7, y + 20, 10, 5, duckBROWN1);
    tft.fillRect(x + 2, y + 5, 10, 15, duckBROWN2);
    tft.fillRect(x + 2, y, 5, 15, duckBROWN3);

    tft.fillRect(x + 22, y + 12, 3, 3, duckORANGE1);
    tft.fillRect(x + 22, y + 17, 3, 3, duckORANGE1);

    tft.fillRect(x + 7, y + 35, 5, 5, duckORANGE1);
    tft.fillRect(x + 7, y + 35, 5, 2, duckORANGE2);

    tft.fillRect(x, y + 25, 12, 10, duckGREEN1);
    tft.fillRect(x, y + 25, 5, 10, duckGREEN2);

    tft.fillRect(x + 2, y + 30, 5, 5, WHITE);
    tft.fillRect(x + 4, y + 33, 3, 2, BLACK);
}

//------------------------------------------------------------------------------

// DEBUGGING
void debug() {
    switch (STATE) {
        case MENU:
            Serial.print(F("MENU"));
            break;
        case CREDITS:
            Serial.print(F("CREDITS"));
            break;
        case START:
            Serial.print(F("START"));
            break;
        case ROUND:
            Serial.print(F("ROUND"));
            break;
        case END:
            Serial.print(F("END"));
            break;
    }

    Serial.print(F(" - X: "));
    Serial.print(duckCurX);
    Serial.print(F(" Y: "));
    Serial.print(duckCurY);
    Serial.print(F(" - moves: "));
    Serial.print(moves);

    Serial.print(F(" - "));
    switch (curDuckState) {
        case upLtR:
            Serial.print(F("upLtR"));
            break;
        case upRtL:
            Serial.print(F("upRtL"));
            break;
        case lineLtR:
            Serial.print(F("lineLtR"));
            break;
        case lineRtL:
            Serial.print(F("lineRtL"));
            break;
        case DEAD:
            Serial.print(F("DEAD"));
            break;
        case downLtR:
            Serial.print(F("downLtR"));
            break;
        case downRtL:
            Serial.print(F("downRtL"));
            break;
        case flyAWAY:
            Serial.print(F("flyAWAY"));
            break;
        case DONE:
            Serial.print(F("DONE"));
            break;
    }

    Serial.print(F(" - "));
    Serial.print((int)touchEND);
    Serial.print(F("ms"));

    Serial.print(F(" - "));
    Serial.println((int)(millis() - roundStart));
}

//------------------------------------------------------------------------------

// SETUP
void setup() {
    // Serial start
    Serial.begin(9600);

    uint16_t ID = tft.readID();

    // screen start
    tft.begin(ID);

    // screen test
    tft.fillScreen(0x001F);
    tft.fillScreen(0x0000);

    // rotate screen
    tft.setRotation(1);

    // no SD card
    bool good = SD.begin(10);
    if (!good) {
        Serial.println(F("SD card error!"));
        while (1);
    }

    Serial.println(F("SETUP PASS"));

    background();
    tft.fillRect(147, 223, 186, 66, NEON);
    menu();
    STATE = MENU;
    NEXT_STATE = MENU;
}

//------------------------------------------------------------------------------

// MAIN LOOP AKA STATE MACHINE
void loop() {
    switch (STATE) {
        case MENU:
            if (initState) {
                sky();
                menu();
                initState = 0;
            }
            touch();
            if (startButton()) {
                initState = 1;
                nullTouch();
                NEXT_STATE = START;
            } else if (multiButton()) {
                initState = 1;
                nullTouch();
                NEXT_STATE = CREDITS;
            }
            break;
        case CREDITS:
            if (initState) {
                sky();
                creditsScreen();
                initState = 0;
            }
            touch();
            if (multiButton()) {
                initState = 1;
                nullTouch();
                NEXT_STATE = MENU;
            }
            break;
        case START:
            if (initState) {
                background();
                initState = 0;
                tft.setTextSize(10);
                roundCount = 2;
                finnalScore = 0;
            }
            switch (start) {
                case 0:
                    start = 3;
                    initState = 1;
                    NEXT_STATE = ROUND;
                    break;
                default:
                    sky();
                    startCount();
                    start--;
                    break;
            }
            break;
        case ROUND:
            if (initState) {
                sky();
                initState = 0;
                bulletsCount = 3;
                bulletDisplay();
                initDuck();
                roundStart = millis();
                disableEDGE = 0;
                Serial.println(F("ROUND START"));
            }

            if (curDuckState != DEAD && curDuckState != flyAWAY && curDuckState != DONE && disableEDGE) {
                edgeDetect();
            }

            duck();
            disableEDGE = 1;

            if (curDuckState != DEAD && curDuckState != flyAWAY && curDuckState != DONE) {
                touchSTART = millis();
                while (millis() - touchSTART <= 35) {
                    touch();

                    if (touchX != 0 && touchY != 0) {
                        if (kill()) {
                            killTime = millis() - roundStart;
                            score();
                            bulletsCount--;
                            sky();
                            curDuckState = nextDuckState = DEAD;
                            Serial.println(F("KILL"));
                        } else if (miss()) {
                            bulletsCount--;
                        }

                        nullTouch();
                        bullets();
                    }
                }

                touchEND = millis() - touchSTART;

                if (bulletsCount == 0 && curDuckState != DEAD) {
                    Serial.println(F("NO AMMO"));
                    sky();
                    curDuckState = nextDuckState = flyAWAY;
                } else if (millis() - roundStart >= roundEnd && curDuckState != DEAD) {
                    Serial.println(F("TIME"));
                    sky();
                    curDuckState = nextDuckState = flyAWAY;
                }
            }

            if (curDuckState == DONE && roundCount == 0) {
                NEXT_STATE = END;
                initState = 1;
                nullTouch();
            } else if (curDuckState == DONE && roundCount != 0) {
                initState = 1;
                roundCount--;
                nullTouch();
            }
            break;
        case END:
            if (initState) {
                background();
                tft.fillRect(147, 223, 186, 66, NEON);
                endScreen();
                initState = 0;
                Serial.println(F("END SCREEN"));
            }
            touch();
            if (multiButton()) {
                initState = 1;
                nullTouch();
                NEXT_STATE = MENU;
            }
            break;
    }

    STATE = NEXT_STATE;
    debug();
}
