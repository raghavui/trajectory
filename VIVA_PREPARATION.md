# Viva Preparation — Trajectory (Career Skill Learning Path Planner)

Simple notes to help you explain the project confidently.

---

## 1. Project Introduction

The **Trajectory** application is a desktop application made in **Core Java** using **Swing** for the screens and **text files** for saving data.

A student registers, logs in and chooses a career such as *Java Developer*. The application then shows a ready-made list of skills for that career, arranged as **Beginner → Intermediate → Advanced**. The student marks each skill as *Not Started*, *In Progress* or *Completed*, and the app calculates the completion percentage. Students can also create personal learning goals with target dates.

## 2. Problem Statement

Students often don't know which skills a career needs, learn in a random order, and have no easy way to see their progress. This project gives each student a clear roadmap, progress tracking and goal management — offline, simple and private.

## 3. Why I Selected This Project

- It solves a real problem that I and my classmates face while preparing for placements.
- It naturally uses all the main Core Java topics: OOP, collections, exception handling, file handling and Swing.
- It has clear modules (login, career, roadmap, progress, goals, profile), so it is easy to design, divide and test.
- It works without internet or a database, so it is easy to run anywhere.

## 4. Explanation of Each Module

| # | Module | Simple explanation |
|---|---|---|
| 1 | Welcome Screen | First window. Shows the app name and description, with **Get Started**, **Login** and **Register** buttons. |
| 2 | Registration | Form with name, email, password, confirm password, college and year. Every field is checked. Duplicate emails are blocked. The password is hashed, then the student is saved in `students.txt`. |
| 3 | Login | Finds the student by email, hashes the typed password with the saved salt and compares. On success the student becomes the "current student" (session) and the dashboard opens. |
| 4 | Dashboard | Greets the student, shows 6 cards (career, total skills, completed, pending, progress %, active goals), a roadmap summary with progress bars, the next recommended skills, recent activities and quick buttons. All numbers are calculated from saved data. |
| 5 | Career Selection | Lists 7 careers. Clicking one shows description, duration, number of skills and levels. "Select This Career" saves it. Changing career asks for confirmation; old progress is kept. |
| 6 | Skill Recommendation | Rule-based (not AI). Each career has predefined skills. The app recommends: first the skills already in progress, then not-started skills from the lowest unfinished level. |
| 7 | Learning Roadmap | Skills shown in three cards (Beginner, Intermediate, Advanced) with description, duration, a coloured status badge and an **Update Status** button. Grey = Not Started, amber = In Progress, green = Completed. |
| 8 | Progress Tracking | Shows total, completed, in-progress and not-started counts, the percentage, the formula, and a table of all skills. |
| 9 | Goal Management | Add, edit, complete, delete and filter goals. Dates must be in `YYYY-MM-DD` format and not in the past. Overdue goals are shown in red. |
| 10 | Profile | Shows the student's details. Name, college and year can be edited. Email can only be changed after entering the current password. Password can also be changed. |
| 11 | Logout | Clears the session and opens the login screen. Saved data is not deleted. |

## 5. Explanation of Important Classes

### model package (data)
- **User** — *abstract* class with name, email, password hash and salt. Has abstract method `getRole()`.
- **Student** — extends `User`; adds college, year and selected career. `toRecord()` converts it to file fields; `fromRecord()` builds it back.
- **Career** — name, description, duration and an `ArrayList<Skill>`. `addSkill()` creates skill IDs like `JAVA-B01`.
- **Skill** — one skill with ID, name, description, career, level, duration and status. Has a copy constructor.
- **SkillStatus** — `enum` with `NOT_STARTED`, `IN_PROGRESS`, `COMPLETED`. `fromText()` rejects invalid values.
- **LearningPath** — one student's skills for one career, grouped by level in a `HashMap<String, List<Skill>>`.
- **Goal** — goal ID, owner email, title, description, target date, status. `isOverdue()` checks the date.
- **Activity** — one line of the "recent activities" list.
- **Persistable** — interface with `toRecord()`; implemented by `Student`, `Goal`, `Activity`.

### repository package (files)
- **FileManager** — the only class that touches files. Reads lines with `BufferedReader`, appends with `FileWriter(…, true)`, rewrites with `PrintWriter` through a temporary file. Escapes special characters.
- **StudentRepository / SkillRepository / GoalRepository / ActivityRepository** — each handles one file and converts lines into objects.

### service package (logic)
- **AuthenticationService** — register, login, logout, update profile, change email, change password. Holds `currentStudent`.
- **CareerService** — the career catalogue in a `HashMap<String, Career>` and `selectCareer()`.
- **RecommendationService** — `getRecommendedSkills()`, `generateLearningPath()`, `getNextRecommendedSkills()`, `getCurrentLevel()`.
- **ProgressTracker** — `calculatePercentage()` (overloaded), `countByStatus()`, `getLevelProgress()`, `updateSkillStatus()`.
- **GoalManager** — `addGoal()`, `updateGoal()`, `markCompleted()`, `deleteGoal()`, `getPendingGoals()`.
- **AppServices** — creates all repositories and services once and shares them with every screen.

### gui package (screens)
- **BaseFrame** — parent of all windows: size, centring, error dialogs, branding panel.
- **WelcomeFrame, LoginFrame, RegisterFrame** — the first three screens.
- **DashboardFrame** — sidebar + `CardLayout` holding all dashboard screens; `showPanel()` switches screens.
- **BasePanel** — abstract parent of all dashboard screens (header, scrolling body, helpers).
- **HomePanel, CareerSelectionPanel, LearningRoadmapPanel, ProgressPanel, GoalManagementPanel, ProfilePanel** — one per module.
- **components** — custom widgets: `ModernButton`, `SidebarButton`, `RoundedPanel`, `StatCard`, `StatusBadge`, `ModernProgressBar`, `ScrollablePanel`, `UIFactory`.

### util package
- **PasswordUtil** — salt generation and SHA-256 hashing.
- **ValidationUtil** — email, name, password, academic year and date checks.
- **ValidationException** — custom exception for user mistakes.
- **UIConstants** — all colours and fonts.

## 6. OOP Concepts Used

| Concept | Meaning in simple words | Example in this project |
|---|---|---|
| Class & Object | Class = blueprint, object = real thing made from it | `new Student(...)`, `new Goal(...)` |
| Encapsulation | Hide data using `private`, allow access through methods | All fields in `Student` are private with getters/setters |
| Inheritance | A child class reuses a parent class | `Student extends User`, `LoginFrame extends BaseFrame`, `HomePanel extends BasePanel` |
| Abstraction | Show *what* to do, hide *how*; abstract classes/interfaces | `abstract class User` with `abstract getRole()`; `abstract class BasePanel` |
| Interface | A contract of methods a class must provide | `Persistable` (`toRecord()`), `Refreshable` (`refreshData()`) |
| Polymorphism | One name, many forms | `DashboardFrame` keeps every screen as a `BasePanel` and calls `panel.refreshData()` — each screen runs its own version |
| Constructor | Special method that creates an object | `Student` has two constructors (overloading); `Skill(Skill original)` is a copy constructor |
| Method Overloading | Same method name, different parameters (compile time) | `calculatePercentage(int, int)`, `calculatePercentage(List<Skill>)`, `calculatePercentage(LearningPath)` |
| Method Overriding | Child class redefines a parent method (run time) | `getRole()` in `Student`, `toString()`, `paintComponent()` in custom components |

## 7. ArrayList and HashMap

**ArrayList** — a resizable list that keeps items in order and allows duplicates.
- `List<Skill>` inside `Career` stores its skills in roadmap order.
- `List<Goal>` returned by `GoalRepository.findByStudent()`.
- `List<String[]>` returned by `FileManager.readRecords()` (one array per line).

**HashMap** — stores *key → value* pairs; finding a value by key is very fast.
- `CareerService`: career name → `Career` object.
- `LearningPath`: level name → list of skills in that level.
- `SkillRepository.loadStatuses()`: skill ID → saved status, used to apply a student's progress to the skill list.
- `DashboardFrame`: screen name → panel, and screen name → sidebar button.

We used `LinkedHashMap` (a type of HashMap) where the order matters, e.g. Beginner should come before Advanced.

## 8. File Handling

- Data is stored in the `data` folder: `students.txt`, `progress.txt`, `goals.txt`, `activities.txt`.
- **One line = one record**, fields separated by `|`. Example: `riya@college.edu|JAVA-B01|COMPLETED`.
- **Reading:** `BufferedReader(new FileReader(file))` + `readLine()` in a loop.
- **Adding:** `new FileWriter(file, true)` — the `true` means *append*, so a new registration never overwrites old students.
- **Updating/deleting:** read all records into a list, change the list, write everything to a temporary file, then replace the real file. If the app crashes while writing, the old file is still safe.
- **Special characters:** `|` is saved as `\p`, a new line as `\n`, and `\` as `\\`, so a goal description can contain any text.
- **try-with-resources** (`try (BufferedReader r = ...)`) closes files automatically.

## 9. Exception Handling

- **ValidationException** (our custom checked exception) — thrown for user mistakes like an invalid email or a past date. The GUI shows its message in a yellow warning dialog.
- **IOException** — thrown when a file can't be read or written. The GUI shows "Could not read or save data files".
- Every button action in the GUI is wrapped in `try { ... } catch (Exception e) { handleError(e); }`, so wrong input never crashes the app.
- Damaged lines in data files are caught and skipped (`IllegalArgumentException`, `DateTimeParseException`), so one bad line doesn't stop the app.
- `NumberFormatException` is caught while generating goal IDs.

## 10. Recommendation System

It is **rule-based**, not AI. The "rules" are written in code:

1. Each career has a fixed list of skills (written in `CareerService`).
2. When a career is chosen, `RecommendationService.generateLearningPath()` copies those skills and applies the student's saved statuses from `progress.txt`.
3. **Next recommended skills:** first any skill that is *In Progress* (finish what you started), then *Not Started* skills from the lowest level that is not finished yet.
4. **Current level** = the first level that still has an unfinished skill.

Because it uses fixed rules, the same input always gives the same output, and it works offline.

## 11. Progress Calculation

```
Progress % = (Completed Skills ÷ Total Skills) × 100
```

- Example: Java Developer has 21 skills. If 8 are completed → 8 ÷ 21 × 100 = 38.1 → **38 %**.
- The result is rounded with `Math.round()`.
- If there are 0 skills, the method returns 0 to avoid dividing by zero.
- Per-level progress uses the same formula on the skills of each level.
- The dashboard and progress screens reload data every time they are opened, so they always show the latest numbers.

## 12. Application Workflow

1. `Main` starts → creates `AppServices` → shows `WelcomeFrame`.
2. Register → validate → hash password → append to `students.txt`.
3. Login → verify hash → store current student → open `DashboardFrame`.
4. Choose Career → save career in `students.txt` → roadmap is generated.
5. Update skill status → save in `progress.txt` → log activity.
6. Dashboard/Progress → calculate statistics from saved data.
7. Goals → add/update/complete/delete in `goals.txt`.
8. Logout → clear current student → back to `LoginFrame`.

---

## 13. 30 Important Viva Questions (with simple answers)

1. **What is your project about?**
   A Java Swing desktop app that gives students a career-based skill roadmap, tracks their skill progress and manages learning goals.

2. **Why did you use Java Swing?**
   It is part of standard Java, needs no extra library, and lets us build desktop windows with buttons, tables and forms.

3. **Why text files instead of a database?**
   The project must be Core Java and work offline without installing anything. Text files are simple, enough for this size, and show file handling.

4. **What is JVM, JRE and JDK?**
   JVM runs Java bytecode. JRE = JVM + libraries to run programs. JDK = JRE + tools (like `javac`) to develop programs.

5. **What is a class and an object?**
   A class is a blueprint (e.g. `Student`); an object is an actual student created with `new Student(...)`.

6. **What is encapsulation? Show it.**
   Keeping fields private and giving access through methods. `Student` fields are private and accessed with `getCollegeName()` / `setCollegeName()`.

7. **Where did you use inheritance?**
   `Student extends User`, all windows extend `BaseFrame`, all dashboard screens extend `BasePanel`.

8. **What is an abstract class? Where is it used?**
   A class that can't be instantiated and may have abstract methods. `User`, `BaseFrame` and `BasePanel` are abstract.

9. **Difference between abstract class and interface?**
   An abstract class can have fields, constructors and normal methods; a class can extend only one. An interface is a contract; a class can implement many. We used both.

10. **Which interfaces did you create?**
    `Persistable` (objects that can be saved: `toRecord()`) and `Refreshable` (screens that can reload: `refreshData()`).

11. **What is polymorphism? Example?**
    One method call behaving differently depending on the object. `panel.refreshData()` runs different code for HomePanel, ProgressPanel, etc.

12. **Overloading vs overriding?**
    Overloading = same name, different parameters, same class (`calculatePercentage`). Overriding = child class redefines parent method (`getRole()`).

13. **What is a constructor? Did you overload one?**
    A special method called when an object is created. Yes — `Student` has two constructors; `LoginFrame` has one with a pre-filled email.

14. **Why use ArrayList instead of an array?**
    ArrayList grows automatically; we don't know how many goals or students there will be.

15. **Why use HashMap?**
    To find things quickly by a key — e.g. find a career by its name, or a skill's status by its ID.

16. **Difference between ArrayList and HashMap?**
    ArrayList stores items by index position; HashMap stores key-value pairs and looks up by key.

17. **How do you store passwords?**
    Never as plain text. We generate a random salt, combine it with the password, hash it with SHA-256 10,000 times and save only the salt and hash.

18. **What is a salt and why is it needed?**
    A random value added before hashing so two students with the same password get different hashes, and pre-computed hash tables don't work.

19. **How does login check the password if it is hashed?**
    It hashes the typed password with the saved salt and compares the result with the saved hash.

20. **How do you prevent duplicate registration?**
    `StudentRepository.emailExists()` checks all saved emails (ignoring upper/lower case) before saving.

21. **How is email validated?**
    With a regular expression in `ValidationUtil.isValidEmail()` that checks the `name@domain.ext` pattern.

22. **What is exception handling?**
    Handling runtime errors with `try`, `catch`, `finally`, `throw`, `throws` so the program doesn't crash.

23. **Checked vs unchecked exceptions?**
    Checked must be handled or declared (`IOException`, our `ValidationException`). Unchecked are runtime errors (`IllegalArgumentException`, `NullPointerException`).

24. **Why did you create a custom exception?**
    To separate user-input mistakes from system errors and show clear messages to the user.

25. **Which file-handling classes did you use?**
    `File`, `FileReader`, `BufferedReader`, `FileWriter`, `BufferedWriter`, `PrintWriter` and `Files.move`.

26. **How do you avoid overwriting existing students?**
    New students are added in append mode: `new FileWriter(file, true)`.

27. **What happens if a user types `|` in a goal?**
    `FileManager` escapes it to `\p` before saving and converts it back when reading, so the data stays correct.

28. **What is CardLayout and why use it?**
    A layout that stacks panels like cards and shows one at a time. It lets us switch screens inside one window instead of opening many windows.

29. **What is event handling in Swing?**
    Responding to user actions. We use `ActionListener` for button clicks, `MouseListener` for hover and clicks on career cards, and `FocusListener` for highlighting fields.

30. **How do you make sure one student can't see another's data?**
    Every progress line and goal stores the owner's email, and every service method filters or checks by the logged-in student's email (user isolation).

## 14. 10 Possible Questions About the Source Code

1. **Where does the program start?**
   `src/main/Main.java` → `main()` sets the look and feel, creates `AppServices("data")` and opens `WelcomeFrame` using `SwingUtilities.invokeLater`.

2. **Why `SwingUtilities.invokeLater`?**
   Swing is not thread-safe; all GUI work must run on the Event Dispatch Thread.

3. **Show the progress formula in code.**
   `ProgressTracker.calculatePercentage(int completed, int total)` → `Math.round(completed * 100.0 / total)`, returns 0 if `total <= 0`.

4. **How is a skill status saved?**
   `ProgressTracker.updateSkillStatus()` → `SkillRepository.saveStatus(email, skillId, status)` reads `progress.txt`, updates or adds the line, and rewrites the file.

5. **How are skill IDs created?**
   In `Career.addSkill()`: prefix + first letter of the level + number in that level, e.g. `JAVA-I03` = 3rd Intermediate Java skill.

6. **Why is `Skill` copied with `new Skill(masterSkill)`?**
   The career catalogue is shared. Copying gives each student their own skill objects so one student's status doesn't change another's.

7. **How is a new goal ID generated?**
   `GoalRepository.generateNextGoalId()` finds the highest number among existing IDs (starting at 1000) and adds 1 → `G1001`, `G1002`, ...

8. **How does the dashboard always show fresh data?**
   `DashboardFrame.showPanel()` calls `refreshData()` on the screen before showing it; each screen reloads from the files.

9. **How does changing the email keep the student's data?**
   `AuthenticationService.changeEmail()` checks the password and duplicates, updates `students.txt`, then calls `changeOwnerEmail()` on the progress, goal and activity repositories.

10. **How are the modern-looking buttons made?**
    `ModernButton` extends `JButton`, turns off default painting and overrides `paintComponent()` to draw a rounded rectangle. A `MouseListener` changes the colour on hover.

## 15. How to Explain the Project in 2 Minutes

> "Good morning. My project is **Trajectory**, a desktop application built in **Core Java** with **Swing** and **file handling**.
>
> The problem it solves is that students often don't know which skills to learn for a career, in what order, or how much progress they've made.
>
> In my app, a student **registers** — all fields are validated, duplicate emails are blocked, and the password is stored as a **salted SHA-256 hash**. After **logging in**, they see a **dashboard** with live statistics.
>
> The student **chooses a career** from seven options, like Java Developer or Data Analyst. A **rule-based recommendation system** then generates a **learning roadmap** of predefined skills grouped into **Beginner, Intermediate and Advanced** levels. The student updates each skill's status — Not Started, In Progress or Completed — and the app calculates **progress using (completed ÷ total) × 100**. Students can also **add, edit, complete and delete goals** with target dates, and manage their **profile**.
>
> Everything is saved in **text files** using FileReader, BufferedReader and FileWriter, with special characters escaped, so the data stays after restarting. Each student sees **only their own data**.
>
> Technically, the project uses **inheritance** (Student extends User), **abstraction** (abstract BasePanel), **interfaces** (Persistable, Refreshable), **polymorphism**, **method overloading**, **ArrayList and HashMap**, **custom exceptions**, and Swing **event handling** with **CardLayout** navigation.
>
> I tested it with 21 automatic test cases covering registration, login, career selection, progress calculation, goals and data persistence — all of them pass. Thank you."

## 16. Quick Tips for the Viva

- Run the app before the viva and register a demo account; complete a few skills and add 2 goals so the dashboard has data.
- Keep `data/students.txt` open to show that the password is hashed.
- Be ready to open `ProgressTracker.java` (formula), `FileManager.java` (file handling) and `Student.java`/`User.java` (inheritance).
- If asked "what would you improve?", mention: database with JDBC, admin panel to add careers, reminders for goals, charts.
