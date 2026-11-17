# Test Cases — Trajectory (Career Skill Learning Path Planner)

Two kinds of testing were done:

1. **Automatic tests** — `src/test/AppTestRunner.java` checks the logic (services, files) without opening windows. Run it in the IDE or with `run-tests.bat`. It uses a separate `test-data` folder and deletes it afterwards.
2. **Manual tests** — steps to perform in the running application (useful to demonstrate in the viva).

**Latest automatic run:** 21 passed, 0 failed (14 required test cases + 7 extra).

| ID | Test Case | Steps (manual) | Input | Expected Result | Status |
|---|---|---|---|---|---|
| TC01 | Successful registration | Welcome → Get Started → fill all fields → Create Account | Riya Sharma, riya.sharma@college.edu, secret123 / secret123, ABC Institute, 3rd Year | "Registration successful!" message; login screen opens with email filled in; new line in `data/students.txt` with a hash (not the real password) | Pass |
| TC02 | Duplicate email registration | Register again with the same email (any letter case) | RIYA.SHARMA@college.edu | Warning: "This email is already registered. Please log in instead." Nothing saved | Pass |
| TC03 | Successful login | Login screen → enter email + password → Login | riya.sharma@college.edu / secret123 | Dashboard opens with "Good …, Riya!" and her email in the sidebar | Pass |
| TC04 | Incorrect password | Login with wrong password | riya.sharma@college.edu / wrong999 | Warning: "Incorrect password. Please try again." Password field cleared, dashboard not opened | Pass |
| TC05 | Empty / invalid registration fields | Click Create Account with empty fields; then invalid email; then mismatching passwords | "", "not-an-email", "abc12345" vs "different1" | Warnings: "All fields are required…", "Please enter a valid email…", "Password and Confirm Password do not match." | Pass |
| TC06 | Career selection | Choose Career → click Java Developer → Select This Career | Java Developer | Success message; roadmap opens; sidebar/dashboard show "Java Developer"; career saved in `students.txt`. Selecting an unknown career is rejected | Pass |
| TC07 | Skill recommendation | Open Choose Career / Learning Roadmap | Java Developer | 21 skills: 7 Beginner (starting with "Java Introduction"), 8 Intermediate, 6 Advanced | Pass |
| TC08 | Updating skill status | Learning Roadmap → Update Status on a skill → choose Completed | Java Introduction → Completed | Badge turns green, tick shown, activity logged, line saved in `progress.txt`. Invalid status text (e.g. "Done!!") is rejected by `SkillStatus.fromText` | Pass |
| TC09 | Progress percentage | Complete 4 of 21 skills and open My Progress | 4 completed / 21 total | 19 % ((4 ÷ 21) × 100 = 19.05, rounded). Also 4/10 = 40 %, 0/0 = 0 % (no crash) | Pass |
| TC10 | Adding a goal | My Goals → fill form → Add Goal | "Finish OOP \| part 1", description with new line and `\`, date 10 days ahead | Goal appears as Pending with ID G1001; special characters kept. Invalid date "2026-02-30", past date and empty title are rejected | Pass |
| TC11 | Updating a goal / completing it | Select goal → Edit Selected → change → Update Goal; then Mark as Completed | Title "Finish OOP", description "Updated" | Table shows new values; after completion status = Completed and pending count = 0 | Pass |
| TC12 | Deleting a goal | Select goal → Delete → Yes | Temp goal | Goal removed from table and from `goals.txt`. Another student cannot delete this goal (user isolation) | Pass |
| TC13 | Logging out | Sidebar → Logout → Yes | — | Login screen opens; session cleared (`getCurrentStudent()` is null) | Pass |
| TC14 | Restart and check saved data | Close the app, start it again, log in | Same account | Career, skill statuses (19 %), goals and recent activities are all still there | Pass |
| EX1 | Special characters in files | Automatic | `a\|b`, text with new lines, `\` | Saved and loaded exactly the same | Pass |
| EX2 | Change email | My Profile → Change Registered Email | new email + wrong password, then correct password | Wrong password rejected; with correct password email changes and progress + goals move to the new email | Pass |

## Additional automatic checks

| ID | Check | Expected | Status |
|---|---|---|---|
| EX3 | Change career from Java Developer to Web Developer and back | Web starts at 0 %; Java progress (19 %) returns when switching back | Pass |
| EX4 | Two different students | The second student sees no career, no goals and 0 % progress of their own | Pass |
| EX5 | Profile update and password change | Invalid year and wrong current password rejected; new name/year/password work after restart | Pass |
| EX6 | Goal with a past target date | Shown as "Overdue" | Pass |
| EX7 | Damaged line in a data file | Line skipped with a console message; other students still load | Pass |

## Manual checks to do in the running app

All screens were rendered and visually checked at the default window size. Please also try these by hand before the viva:

| Check | Expected |
|---|---|
| Resize / maximise the dashboard window | Layout stretches, nothing overlaps; long pages scroll vertically |
| Click every sidebar item and every quick-action button | The correct screen opens and shows current data |
| Update a skill on the roadmap, then open the Dashboard | Cards and progress bars show the new numbers |
| Edit a goal, then click Clear | Form returns to "Add a New Goal" |
