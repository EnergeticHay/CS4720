# App 1: UVA Bucket List

**Name:** Jason Lu
**Computing ID:** jbz2et
**Course:** CS 4720 Mobile Application Development

Users add bucket list items, each with a name and due date, and check them off as they complete them. 
The list keeps incomplete items first, sorted by earliest due date.

## Notes

- MainActivity is the List screen. CreateActivity and DetailActivity are connected by explicit intents, passing only the item ID.
- Each Activity has its own ViewModel. The shared list lives in `BucketRepository`, per the companion notes.
- `DueDateField.kt` holds the Material 3 DatePicker, shared by Create and Detail.
- Custom navy/orange/cream theme with serif headings. Dynamic color is off so the theme always shows. Dark theme is supported.
- minSdk is 26 for `java.time.LocalDate`. Data does not persist across app restarts, per the spec.

## AI Usage Note

I used Claude to structure the app from the instructions and companion notes.
For the most part, Claude was only used to check for errors, and not for initial code. The exception was to help me implement DatePicker, which was not covered in class.
I verified my code by running it in the emulator and tested two different color themes.
