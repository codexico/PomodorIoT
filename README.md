# PomodorIoT
arduino controlled Pomodoro

## Build
![Breadboard](PomodorIoT_bb.png)

## Install
`` npm install ``

## Run
`` node index.js ``

It will start the pomodoro automatically.

Or use the Arduino IDE to run `` main/main.ino ``

Or run it online:
https://circuits.io/circuits/2651548-pomodoriot


## API

### http://localhost:3000/finish
Finishes the running pomodoro sequence

### http://localhost:3000/start
Finishes the running pomodoro sequence and starts a new one.
