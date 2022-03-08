// Arduino UNO

#include <SPI.h>
#include <SD.h>

#include <MCUFRIEND_kbv.h>
MCUFRIEND_kbv tft;

#define BLACK 0x0000

// lists all the files on SD card recursively
void printDirectory(File dir, int numTabs) {
  while (true) {

    // opens next file in the directory
    File entry = dir.openNextFile();
    // no more files = ends the program
    if (!entry) {
      break;
    }

    // tabulators to indicate hierarchy of files
    for (uint8_t i = 0; i < numTabs; i++) {
      tft.print("     ");
      Serial.print('\t');
    }

    // prints the name of a file
    tft.print(entry.name());
    Serial.print(entry.name());

    if (entry.isDirectory()) {
      // if its a directory printDirectory is called (recursion)
      tft.println("/");
      Serial.println("/");
      printDirectory(entry, numTabs + 1);
    } else {
      // its a normal file so we also print its size
      tft.print("     ");
      Serial.print("\t\t");
      int size = entry.size();
      tft.println(size);
      Serial.println(size);
    }
    entry.close();
  }
}

void setup() {
  Serial.begin(9600);

  Serial.print("Initializing SD card...");

  // display initialization
  uint16_t ID = tft.readID();
  tft.begin(ID);
  tft.fillScreen(BLACK);
  tft.setTextSize(2);

  // SD card check
  if (!SD.begin(10)) {
    // SD card failed
    Serial.println("Initialization failed!");
  } else {
    // SD card is OK
    File root;
    root = SD.open("/");
    Serial.println("Initialization done.");
    printDirectory(root, 0);
    Serial.println("All files listed!");
  }
}

void loop() {}
