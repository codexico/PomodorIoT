#include <SoftwareSerial.h>  
SoftwareSerial btSerial(11, 10); // RX, TX 

const int POMODOROS = 4;

const int BUTTON = 2;

// PWM leds
const int YELLOW1 = 3;
const int YELLOW2 = 5;
const int YELLOW3 = 6;
const int YELLOW4 = 9;

// digital leds
const int GREEN1 = 4;
const int GREEN2 = 7;
const int GREEN3 = 8;
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

bool running = false;

// put your setup code here, to run once:
void setup() {
  // the array elements are numbered from 0 to (pinCount - 1).
  // use a for loop to initialize each pin as an output:
  for (int thisPin = 0; thisPin < pinCount; thisPin++) {
    pinMode(ledPins[thisPin], OUTPUT);
  }
  
  pinMode(BUTTON, INPUT);  
  btSerial.begin(57600);
  Serial.begin(9600);
}

void _delay(unsigned long duration) {  
 unsigned long start = millis();

 while (millis() - start <= duration) {
   //watchButton();  // check the buttons 
   watchBluetooth();
 }
}

void watchButton() {
  buttonState = digitalRead(BUTTON);

  if (buttonState == HIGH) {
    Serial.println("BTN::HIGH");
    startPomodoroSequence();
  } else {
    Serial.println("BTN::LOW");
    stopPomodoroSequence();
  }
}

void watchBluetooth2(){
  if(btSerial.available()){
    if(running == false){
      Serial.println("Connected");
      startPomodoroSequence();
    }
  }else{
    if(running == true){
      Serial.println("Not Connected");
      stopPomodoroSequence();
    }
  }
}

void watchBluetooth(){ 
  String command = "";

  while(btSerial.available()) {
      command = btSerial.readStringUntil('\n');
  }

  if (command == "1") {
    Serial.println("BT::HIGH");
    startPomodoroSequence();
  } else if (command == "0") {
    Serial.println("BT::LOW");
    stopPomodoroSequence();
  }
}

void startPomodoroSequence(){
  if(running == true) return;
  running = true;
  
  for (int thisPin = 0; thisPin < 3; thisPin++) {
    digitalWrite(ledPins[thisPin * 2], HIGH);
    _delay(WORK_DURATION);
    if(running == false) return;

    digitalWrite(ledPins[(thisPin * 2) + 1], HIGH);
    _delay(BREAK_DURATION);
    if(running == false) return;
  }

  digitalWrite(ledPins[6], HIGH);
  _delay(WORK_DURATION);
  if(running == false) return;

  digitalWrite(ledPins[7], HIGH);
  _delay(BIG_BREAK_DURATION);
  if(running == false) return;

  stopPomodoroSequence();
  startPomodoroSequence();
}

void stopPomodoroSequence(){
  if(running == true){
    running = false;
    for (int thisPin = 0; thisPin < pinCount; thisPin++) {
      digitalWrite(ledPins[thisPin], LOW);
    }
  }
}

// put your main code here, to run repeatedly:
void loop() {
  // start watcher
  watchBluetooth();  
}
