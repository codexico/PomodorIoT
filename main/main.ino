const int POMODOROS = 4;

const int BUTTON = 2;

// PWM leds
const int YELLOW1 = 3;
const int YELLOW2 = 5;
const int YELLOW3 = 6;
const int YELLOW4 = 9;

// digital leds
const int GREEN1 = 8;
const int GREEN2 = 4;
const int GREEN3 = 7;
const int RED = 13;

// durations
const int WORK_DURATION = 3000;
const int BREAK_DURATION = 3000;
const int BIG_BREAK_DURATION = 6000;

// initialize leds
int ledPins[] = {
    YELLOW1,
    GREEN1,
    YELLOW2,
    GREEN2,
    YELLOW3,
    GREEN3,
    YELLOW4,
    RED
};
int pinCount = 8;

int buttonState = 0; // variable for reading the pushbutton status

// put your setup code here, to run once:
void setup() {
  // the array elements are numbered from 0 to (pinCount - 1).
  // use a for loop to initialize each pin as an output:
  for (int thisPin = 0; thisPin < pinCount; thisPin++) {
    pinMode(ledPins[thisPin], OUTPUT);
  }
  
  pinMode(BUTTON, INPUT);
  
  Serial.begin(9600);  
}

// put your main code here, to run repeatedly:
void loop() {
    // read the state of the pushbutton value:
  buttonState = digitalRead(BUTTON);

  // check if the pushbutton is pressed.
  // if it is, the buttonState is HIGH:
  if (buttonState == HIGH) {
    Serial.println("buttonState = HIGH");
  } else {
    Serial.println("buttonState = LOW");
  }


  for (int thisPin = 0; thisPin < 3; thisPin++) {
    digitalWrite(ledPins[thisPin * 2], HIGH);
    delay(WORK_DURATION);

    digitalWrite(ledPins[(thisPin * 2) + 1], HIGH);
    delay(BREAK_DURATION);
  }

  digitalWrite(ledPins[6], HIGH);
  delay(WORK_DURATION);

  digitalWrite(ledPins[7], HIGH);
  delay(BREAK_DURATION);

  for (int thisPin = 0; thisPin < pinCount; thisPin++) {
    digitalWrite(ledPins[thisPin], LOW);
  }
}

