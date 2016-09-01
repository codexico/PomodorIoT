var five = require('johnny-five');
var board = new five.Board();

board.on('ready', function () {

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

    // config steps
    let step1 = {
        work: {
            led: leds[0],
            duration: WORK_DURATION
        },
        break: {
            led: leds[1],
            duration: BREAK_DURATION
        }
    };

    let step2 = {
        work: {
            led: leds[2],
            duration: WORK_DURATION
        },
        break: {
            led: leds[3],
            duration: BREAK_DURATION
        }
    };

    let step3 = {
        work: {
            led: leds[4],
            duration: WORK_DURATION
        },
        break: {
            led: leds[5],
            duration: BREAK_DURATION
        }
    };

    let step4 = {
        work: {
            led: leds[6],
            duration: WORK_DURATION
        },
        break: {
            led: leds[7],
            duration: BIG_BREAK_DURATION
        }
    };

    let steps = [step1, step2, step3, step4];

    function finishPomodoro() {
        leds.map((led) => {
            led.off();
        });
    }

    function nextStep(index) {
        let next_step = index + 1;
        if (next_step === steps.length) {
            next_step = 0;
            finishPomodoro();
        }
        return next_step;
    }

    function execute(index) {
        let step = steps[index];

        // start pomodoro
        step.work.led.fadeIn(step.work.duration);

        // break
        board.wait(step.work.duration, function () {
            step.break.led.on();
            board.wait(step.break.duration, function () {
                execute(nextStep(index));
            });
        });
    }

    // start
    execute(0);
});
