var five = require('johnny-five');
var board = new five.Board();

board.on('ready', function () {

    const POMODOROS = 4;

    // PWM leds
    const YELLOW1 = 3;
    const YELLOW2 = 5;
    const YELLOW3 = 6;
    const YELLOW4 = 9;

    // digital leds
    const GREEN1 = 2;
    const GREEN2 = 4;
    const GREEN3 = 7;
    const RED = 13;

    // durations
    const WORK_DURATION = 3000;
    const BREAK_DURATION = 3000;
    const BIG_BREAK_DURATION = 6000;

    // initialize leds
    let leds = new five.Leds([
        YELLOW1,
        GREEN1,
        YELLOW2,
        GREEN2,
        YELLOW3,
        GREEN3,
        YELLOW4,
        RED
    ]);

    function finishPomodoro() {
        leds.map((led) => {
            led.off();
        });
    }

    function nextStep(index) {
        let next_step = index + 1;
        if (next_step === POMODOROS) {
            next_step = 0;
            finishPomodoro();
        }
        return next_step;
    }

    function execute(index) {
        // start pomodoro
        leds[2 * index].fadeIn(WORK_DURATION);

        // break
        board.wait(WORK_DURATION, function () {
            leds[(2 * index) + 1].on();

            let break_duration = BREAK_DURATION;

            if ((index + 1) === POMODOROS) {
                // big break on last pomodoro
                break_duration = BIG_BREAK_DURATION;
            }
            board.wait(break_duration, function () {
                execute(nextStep(index));
            });
        });
    }

    // start
    execute(0);
});
