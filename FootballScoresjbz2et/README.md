# App 2: College Football Scores

**Name:** Jason Lu
**Computing ID:** jbz2et
**Course:** CS 4720 Mobile Application Development

Fetches college football scores from the NCAA API, stores them in a Room database, and keeps them browsable by week and division with no network.

## Notes

- I treat Sep 1, 2026 as the start of week 1 and count 7 days per week from there. The app caps it between weeks 1 and 14.
- Weeks 13 and 14 have no games, so you can see the "No games this week" screen there.
- To see Error state, turn on airplane mode and pick a week you haven't opened yet. Weeks you already opened still work offline.
- There's a spinner when nothing has loaded yet, and a thin bar under the controls every time it fetches.
- Some games show up in both FBS and FCS with the same ID, so I used game ID + division as the database key.
- OT games say "FINAL (OT)" in `currentPeriod`.
- Future games may show "TBA" as I opted to follow website conventions.
- I chose a flat package layout over the suggested sub-packages, for simplicity and because I started this assignment before we learned about nest/flattened structures. For instance, I did not use a separate .ui.theme package structure for Color/Theme/Type.kt.
## AI usage

I used Claude throughout this assignment - it helped me plan and break the assignment down into smaller steps.
First, I explored and utilized the API JSON myself.
Next, I wrote first drafts of the code in every layer except for Color, Theme, and Type.kt, which I used Claude to generate an appealing light purple theme.
I tested each file step-by-step: Logcat checks that week 5 parsed to 59 games, a Room test that upserting 14-10 then 24-17 left one row, airplane-mode tests for cached and uncached weeks, rotation tests, and a Compose preview for the live row.