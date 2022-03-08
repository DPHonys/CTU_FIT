// Arduino UNO

char *letters[] = {
    ".-", "-...", "-.-.", "-..", ".", "..-.", "--.", // A-G
    "....", "..", ".---", "-.-", ".-..", "--", "-.", // H-N
    "---", ".--.", "--.-", ".-.", "...", "-", "..-", // O-U
    "...-", ".--", "-..-", "-.--", "--.."            // V-Z
};

#define DOT 200          // dot duration
#define DASH 800         // dash duration
#define NO_LIGHT 200     // no light duration
#define PAUSE_LETTER 500 // pause duration between letters
#define PAUSE_WORD 1000  // pause duration between words

#define LEDPIN 3

int incomingByte = 0;
int newWord = 1;

void setup() {
  pinMode(LEDPIN, OUTPUT);
  Serial.begin(9600);
}

void flash(int duration) {
  digitalWrite(LEDPIN, HIGH);
  delay(duration);
  digitalWrite(LEDPIN, LOW);
  delay(NO_LIGHT);
}

void flashSequence(char *sequence) {
  for (int i = 0; i < strlen(sequence); i++) {
    if (sequence[i] == '.') {
      flash(DOT);
    } else {
      flash(DASH);
    }
  }
}

void loop() {

  if (Serial.available() > 0) {
    newWord = 0;
    // read incoming data from serial port
    incomingByte = Serial.read();

    // a-z 97-122
    // A-Z 65-90
    // space 32
    if (incomingByte >= 97 && incomingByte <= 122) {
      Serial.print(letters[(incomingByte - 97)]);
      Serial.print(' ');
      flashSequence(letters[(incomingByte - 97)]);
    } else if (incomingByte >= 65 && incomingByte <= 90) {
      Serial.print(letters[(incomingByte - 65)]);
      Serial.print(' ');
      flashSequence(letters[(incomingByte - 65)]);
    } else if (incomingByte == 32) {
      delay(PAUSE_WORD);
    } else {
      delay(PAUSE_LETTER);
    }
  } else if (!newWord) {
    Serial.println("");
    newWord = 1;
  }
}
