# App 3: Step and Activity Logger

**Name:** Jason Lu
**Computing ID:** jbz2et
**Course:** CS 4720 Mobile Application Development

Reads the accelerometer live, counts steps with the hardware step counter when the device has one or my own peak detector when it doesn't, and saves each day's total in Room with a history list and bar chart.

## Notes

- On the emulator there is no step counter, so the app uses my accelerometer counter. The steps card says which method is active.
- Tap "Turn on step counting" to see the permission request. To test steps, open Extended Controls > Virtual sensors, switch to Move, and drag the phone quickly back and forth.
- I ask for ACTIVITY_RECOGNITION before counting with either method, even though the accelerometer alone doesn't require it. Step data is activity data no matter which sensor produces it, so the user gets the same choice on every device.
- If permission is denied, the accelerometer card keeps running and the steps card explains how to turn it on in Settings, with an Open Settings button.
- The chart shows the last 7 saved days, so on a fresh install there is only one bar.
- I used a two-tab bottom bar instead of Navigation Compose because there are only two screens.

## Sampling rate

I registered the accelerometer at SENSOR_DELAY_GAME (about 50 readings per second) and the hardware step counter at SENSOR_DELAY_NORMAL. A step shows up as a short spike in acceleration, and people take about 2 steps per second, so my counter needs several readings per spike. NORMAL (5 per second) would miss or flatten most peaks, and UI (16 per second) is borderline. GAME is the slowest setting that reliably catches each spike, while FASTEST would wake the CPU more often for no accuracy gain. The hardware counter runs on a low-power chip and only reports when the total changes, so the slowest setting is enough there. I cut battery cost in three more places. Both listeners are unregistered in onPause, the screen redraws the x, y, z values 10 times a second instead of 50, and Room is written at most every 2 seconds plus once in onPause instead of on every step. If I needed more savings, I could pass a max report latency to registerListener so readings are batched, as shown in the Sensing I lecture.

My own counter follows the method from class. It takes the magnitude sqrt(x² + y² + z²), averages the last 4 readings to smooth it, and counts a step at each peak above 11.0 m/s² that comes at least 0.3 seconds after the last one. At rest the magnitude sits near gravity (about 9.8), so the threshold has to sit clearly above that to ignore small hand movements but still catch a real jolt. I started at 11.0 and used the emulator's virtual sensors to check that the counter reacts to movement. The app shows the live smoothed magnitude next to the threshold so I could watch it while testing. The 0.3 second gap caps the count at about 3 steps per second so one jolt can't be counted twice. I didn't test on a real phone, so with a loaner tablet I would walk a counted 100 steps and adjust the threshold until the two numbers matched.

## Ethics reflection

- **What the data could reveal.** One step total per day sounds harmless, but over several weeks it shows a person's routine: which days they work, when they travel, and when they barely leave home. A sudden drop could point to an illness, an injury or a pregnancy, and steady low numbers could point to a disability. An employer or insurer could use that against someone. The raw accelerometer stream is even more revealing, since research shows gait can identify a person, and combined with location or timestamps it can show where someone is and when their home is empty.
- **Collect only what I need.** The app stores one number per day. It never saves raw accelerometer readings, timestamps of individual steps, or location. The first hardware counter reading is only used as a starting point, so steps from before the app was installed are never recorded.
- **Keep it only as long as I need it.** Days older than 30 days are deleted automatically, and the History screen has a "Delete all step history" button.
- **Keep it on the device.** The app has no INTERNET permission, and I set allowBackup to false so the step database isn't copied into cloud backups.
- **Make sure users understand.** The permission is requested only when the user taps "Turn on step counting", with one plain sentence explaining why, and the app still works if they say no. The system dialog only says "physical activity", which many people won't connect to long-term patterns, so in a real app I would also explain in onboarding what a few months of step data can show, and I would never sync or share it without a separate opt-in.

## AI usage

I used Claude to plan the assignment and break it into steps. For each file, I referenced the class slides and wrote a mock-up before asking Claude to improve the syntax and identify any errors.
After each step, I tested on the emulator before committing and pushing. I wrote this README myself and structured it the same way as in previous assignments.