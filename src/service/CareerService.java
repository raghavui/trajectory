package service;

import model.Career;
import model.Skill;
import model.Student;
import repository.ActivityRepository;
import repository.StudentRepository;
import util.ValidationException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores the predefined career catalogue and handles career selection.
 *
 * The catalogue is kept in a HashMap: key = career name, value = Career object.
 * (LinkedHashMap is used so the careers always appear in the same order.)
 */
public class CareerService {

    private final Map<String, Career> careers = new LinkedHashMap<>();
    private final StudentRepository studentRepository;
    private final ActivityRepository activityRepository;

    public CareerService(StudentRepository studentRepository, ActivityRepository activityRepository) {
        this.studentRepository = studentRepository;
        this.activityRepository = activityRepository;
        loadCareerCatalogue();
    }

    public List<Career> getAllCareers() {
        return new ArrayList<>(careers.values());
    }

    public Career getCareer(String careerName) {
        return careers.get(careerName);
    }

    public boolean isValidCareer(String careerName) {
        return careerName != null && careers.containsKey(careerName);
    }

    /**
     * Saves the chosen career for the student. Progress of previous careers is kept
     * in the file, so switching back later restores the old progress.
     */
    public void selectCareer(Student student, String careerName) throws ValidationException, IOException {
        if (!isValidCareer(careerName)) {
            throw new ValidationException("Please choose a valid career from the list.");
        }
        String previousCareer = student.getSelectedCareer();
        if (careerName.equals(previousCareer)) {
            throw new ValidationException(careerName + " is already your selected career.");
        }
        student.setSelectedCareer(careerName);
        studentRepository.update(student);
        if (previousCareer.isEmpty()) {
            activityRepository.log(student.getEmail(), "Selected career: " + careerName);
        } else {
            activityRepository.log(student.getEmail(), "Changed career from " + previousCareer + " to " + careerName);
        }
    }

    // ------------------------------------------------------------------
    // Predefined career data (the "rules" of the rule-based recommender)
    // ------------------------------------------------------------------

    private void addCareer(Career career) {
        careers.put(career.getCareerName(), career);
    }

    private void loadCareerCatalogue() {
        addCareer(createJavaDeveloper());
        addCareer(createPythonDeveloper());
        addCareer(createWebDeveloper());
        addCareer(createDataAnalyst());
        addCareer(createAiMlEngineer());
        addCareer(createAndroidDeveloper());
        addCareer(createCybersecurityAnalyst());
    }

    private Career createJavaDeveloper() {
        Career c = new Career("Java Developer",
                "Build reliable desktop, web and enterprise applications using Java, "
                        + "one of the most widely used languages in the software industry.",
                "8 - 10 months", "JAVA");
        String b = Skill.BEGINNER, i = Skill.INTERMEDIATE, a = Skill.ADVANCED;
        c.addSkill(b, "Java Introduction", "JDK, JVM, JRE and writing your first Java program.", "1 week");
        c.addSkill(b, "Variables and Data Types", "Primitive types, String, type casting and constants.", "1 week");
        c.addSkill(b, "Operators", "Arithmetic, relational, logical and assignment operators.", "3 days");
        c.addSkill(b, "Conditional Statements", "if, if-else, else-if ladder and switch statements.", "1 week");
        c.addSkill(b, "Loops", "for, while, do-while loops, break and continue.", "1 week");
        c.addSkill(b, "Arrays", "Single and multi-dimensional arrays, searching and sorting.", "1 week");
        c.addSkill(b, "Methods", "Defining methods, parameters, return values and overloading.", "1 week");
        c.addSkill(i, "Classes and Objects", "Creating classes, objects, constructors and the this keyword.", "1 week");
        c.addSkill(i, "Encapsulation", "Private fields, getters, setters and access modifiers.", "4 days");
        c.addSkill(i, "Inheritance", "extends, super keyword and method overriding.", "1 week");
        c.addSkill(i, "Polymorphism", "Compile-time and run-time polymorphism with examples.", "1 week");
        c.addSkill(i, "Abstraction", "Abstract classes and interfaces, and when to use each.", "1 week");
        c.addSkill(i, "Exception Handling", "try, catch, finally, throw, throws and custom exceptions.", "1 week");
        c.addSkill(i, "Collections Framework", "ArrayList, LinkedList, HashMap, HashSet and iterators.", "2 weeks");
        c.addSkill(i, "File Handling", "Reading and writing files with FileReader, BufferedReader and FileWriter.", "1 week");
        c.addSkill(a, "Multithreading", "Thread class, Runnable, synchronization and thread pools.", "2 weeks");
        c.addSkill(a, "SQL", "Tables, queries, joins, constraints and normalization.", "2 weeks");
        c.addSkill(a, "JDBC", "Connecting Java to a database, PreparedStatement and ResultSet.", "1 week");
        c.addSkill(a, "Spring Boot Basics", "REST controllers, dependency injection and Spring Data JPA.", "4 weeks");
        c.addSkill(a, "Project Development", "Build a complete project using Java, SQL and Spring Boot.", "4 weeks");
        c.addSkill(a, "Interview Preparation", "Core Java questions, DSA practice and mock interviews.", "3 weeks");
        return c;
    }

    private Career createPythonDeveloper() {
        Career c = new Career("Python Developer",
                "Write clean Python code for automation, back-end web services and scripting. "
                        + "Python is known for its simple and readable syntax.",
                "6 - 8 months", "PY");
        String b = Skill.BEGINNER, i = Skill.INTERMEDIATE, a = Skill.ADVANCED;
        c.addSkill(b, "Python Setup and Syntax", "Installing Python, using the interpreter and IDLE/VS Code.", "3 days");
        c.addSkill(b, "Variables and Data Types", "int, float, str, bool and type conversion.", "4 days");
        c.addSkill(b, "Control Flow", "if-elif-else, for and while loops, range().", "1 week");
        c.addSkill(b, "Functions", "Defining functions, arguments, default values and return.", "1 week");
        c.addSkill(b, "Lists, Tuples and Dictionaries", "Built-in data structures and their methods.", "1 week");
        c.addSkill(b, "String Manipulation", "Slicing, formatting, f-strings and common string methods.", "4 days");
        c.addSkill(i, "Object-Oriented Python", "Classes, objects, inheritance and dunder methods.", "2 weeks");
        c.addSkill(i, "Modules and Packages", "import system, pip and virtual environments.", "4 days");
        c.addSkill(i, "File Handling", "Reading and writing text, CSV and JSON files.", "1 week");
        c.addSkill(i, "Exception Handling", "try-except-finally and raising custom exceptions.", "4 days");
        c.addSkill(i, "Comprehensions and Lambdas", "List/dict comprehensions, map, filter and lambda.", "4 days");
        c.addSkill(i, "Git and GitHub", "Version control, branches, commits and pull requests.", "1 week");
        c.addSkill(a, "Decorators and Generators", "Advanced functions, yield and iterators.", "1 week");
        c.addSkill(a, "Working with Databases", "SQL basics, SQLite and an ORM such as SQLAlchemy.", "2 weeks");
        c.addSkill(a, "Web Framework (Flask/Django)", "Routing, templates, forms and REST APIs.", "4 weeks");
        c.addSkill(a, "Testing with pytest", "Unit tests, fixtures and test-driven development.", "1 week");
        c.addSkill(a, "Project Development", "Build and deploy a complete Python application.", "4 weeks");
        c.addSkill(a, "Interview Preparation", "Python questions, DSA problems and mock interviews.", "3 weeks");
        return c;
    }

    private Career createWebDeveloper() {
        Career c = new Career("Web Developer",
                "Design and build responsive websites and web applications, "
                        + "from the user interface to the server and database.",
                "7 - 9 months", "WEB");
        String b = Skill.BEGINNER, i = Skill.INTERMEDIATE, a = Skill.ADVANCED;
        c.addSkill(b, "How the Web Works", "Browsers, servers, HTTP, DNS and hosting basics.", "3 days");
        c.addSkill(b, "HTML5", "Page structure, semantic tags, forms and tables.", "1 week");
        c.addSkill(b, "CSS3", "Selectors, box model, colours, fonts and positioning.", "2 weeks");
        c.addSkill(b, "Flexbox and Grid", "Modern layout systems for responsive pages.", "1 week");
        c.addSkill(b, "JavaScript Basics", "Variables, functions, loops, arrays and objects.", "2 weeks");
        c.addSkill(b, "Git and GitHub", "Version control and publishing your code online.", "4 days");
        c.addSkill(i, "DOM Manipulation", "Selecting elements, events and updating the page.", "1 week");
        c.addSkill(i, "Responsive Design", "Media queries and mobile-first design.", "1 week");
        c.addSkill(i, "Modern JavaScript (ES6+)", "let/const, arrow functions, modules, promises and async/await.", "2 weeks");
        c.addSkill(i, "Fetch API and JSON", "Calling REST APIs and displaying data.", "1 week");
        c.addSkill(i, "Front-end Framework", "Components, state and routing in a framework like React.", "4 weeks");
        c.addSkill(a, "Back-end Development", "Building a server and REST APIs (e.g. Node.js/Express).", "4 weeks");
        c.addSkill(a, "Databases", "SQL and NoSQL databases and connecting them to a server.", "2 weeks");
        c.addSkill(a, "Authentication and Security", "Login systems, sessions, hashing and common web attacks.", "1 week");
        c.addSkill(a, "Deployment", "Hosting a full-stack application on the cloud.", "1 week");
        c.addSkill(a, "Portfolio Project", "Build and publish a complete full-stack project.", "4 weeks");
        return c;
    }

    private Career createDataAnalyst() {
        Career c = new Career("Data Analyst",
                "Collect, clean and analyse data to find useful insights, "
                        + "and present them with clear charts and dashboards.",
                "5 - 7 months", "DA");
        String b = Skill.BEGINNER, i = Skill.INTERMEDIATE, a = Skill.ADVANCED;
        c.addSkill(b, "Excel Fundamentals", "Formulas, sorting, filtering and basic charts.", "1 week");
        c.addSkill(b, "Advanced Excel", "Pivot tables, VLOOKUP/XLOOKUP and conditional formatting.", "1 week");
        c.addSkill(b, "Basic Statistics", "Mean, median, mode, variance and standard deviation.", "2 weeks");
        c.addSkill(b, "SQL Basics", "SELECT, WHERE, ORDER BY and aggregate functions.", "2 weeks");
        c.addSkill(b, "Data Types and Data Quality", "Structured data, missing values and data errors.", "4 days");
        c.addSkill(i, "Advanced SQL", "Joins, subqueries, window functions and CTEs.", "2 weeks");
        c.addSkill(i, "Python for Data Analysis", "Python basics with Pandas and NumPy.", "3 weeks");
        c.addSkill(i, "Data Cleaning", "Handling duplicates, outliers and inconsistent values.", "1 week");
        c.addSkill(i, "Data Visualization", "Matplotlib/Seaborn and choosing the right chart.", "2 weeks");
        c.addSkill(i, "Exploratory Data Analysis", "Finding patterns and trends in a dataset.", "1 week");
        c.addSkill(a, "Power BI / Tableau", "Building interactive dashboards and reports.", "3 weeks");
        c.addSkill(a, "Hypothesis Testing", "t-tests, chi-square tests and A/B testing.", "2 weeks");
        c.addSkill(a, "Business Metrics and Storytelling", "KPIs and presenting insights to stakeholders.", "1 week");
        c.addSkill(a, "Capstone Analysis Project", "Analyse a real dataset and publish the findings.", "3 weeks");
        c.addSkill(a, "Interview Preparation", "SQL challenges, case studies and portfolio review.", "2 weeks");
        return c;
    }

    private Career createAiMlEngineer() {
        Career c = new Career("AI/ML Engineer",
                "Build systems that learn from data, from classic machine learning "
                        + "models to deep learning, and put them into real applications.",
                "10 - 12 months", "AIML");
        String b = Skill.BEGINNER, i = Skill.INTERMEDIATE, a = Skill.ADVANCED;
        c.addSkill(b, "Python Programming", "Python syntax, functions, OOP and libraries.", "3 weeks");
        c.addSkill(b, "Linear Algebra", "Vectors, matrices and matrix operations.", "2 weeks");
        c.addSkill(b, "Probability and Statistics", "Distributions, Bayes theorem and statistical measures.", "2 weeks");
        c.addSkill(b, "Calculus for ML", "Derivatives, gradients and the chain rule.", "1 week");
        c.addSkill(b, "NumPy and Pandas", "Working with arrays and tabular data.", "2 weeks");
        c.addSkill(i, "Data Preprocessing", "Scaling, encoding, feature engineering and train/test split.", "1 week");
        c.addSkill(i, "Supervised Learning", "Linear/logistic regression, decision trees and SVM.", "3 weeks");
        c.addSkill(i, "Unsupervised Learning", "K-Means, hierarchical clustering and PCA.", "2 weeks");
        c.addSkill(i, "Model Evaluation", "Accuracy, precision, recall, F1 and cross-validation.", "1 week");
        c.addSkill(i, "Scikit-learn", "Building ML pipelines with scikit-learn.", "2 weeks");
        c.addSkill(a, "Neural Networks", "Perceptrons, activation functions and backpropagation.", "3 weeks");
        c.addSkill(a, "Deep Learning Frameworks", "TensorFlow or PyTorch for building models.", "3 weeks");
        c.addSkill(a, "Computer Vision", "CNNs, image classification and transfer learning.", "3 weeks");
        c.addSkill(a, "Natural Language Processing", "Text processing, embeddings and transformers.", "3 weeks");
        c.addSkill(a, "Model Deployment (MLOps)", "Serving models with an API and monitoring them.", "2 weeks");
        c.addSkill(a, "Capstone ML Project", "Build an end-to-end machine learning project.", "4 weeks");
        return c;
    }

    private Career createAndroidDeveloper() {
        Career c = new Career("Android Developer",
                "Create mobile apps for Android phones and tablets using Kotlin/Java "
                        + "and Android Studio, and publish them on the Play Store.",
                "7 - 9 months", "AND");
        String b = Skill.BEGINNER, i = Skill.INTERMEDIATE, a = Skill.ADVANCED;
        c.addSkill(b, "Kotlin or Java Basics", "Syntax, variables, control flow and functions.", "3 weeks");
        c.addSkill(b, "Object-Oriented Programming", "Classes, objects, inheritance and interfaces.", "2 weeks");
        c.addSkill(b, "Android Studio Setup", "Installing Android Studio, emulator and project structure.", "3 days");
        c.addSkill(b, "Activities and Lifecycle", "Activities, intents and the activity lifecycle.", "1 week");
        c.addSkill(b, "Layouts and Views", "XML layouts, ConstraintLayout and common widgets.", "2 weeks");
        c.addSkill(i, "RecyclerView and Adapters", "Displaying scrollable lists efficiently.", "1 week");
        c.addSkill(i, "Fragments and Navigation", "Fragments, navigation component and back stack.", "1 week");
        c.addSkill(i, "Local Storage", "SharedPreferences and Room database.", "2 weeks");
        c.addSkill(i, "Networking", "REST APIs with Retrofit and JSON parsing.", "2 weeks");
        c.addSkill(i, "Material Design", "Themes, colours and Material components.", "1 week");
        c.addSkill(a, "MVVM Architecture", "ViewModel, LiveData/Flow and repository pattern.", "2 weeks");
        c.addSkill(a, "Coroutines and Background Work", "Asynchronous code and WorkManager.", "2 weeks");
        c.addSkill(a, "Jetpack Compose", "Building UIs with the modern declarative toolkit.", "3 weeks");
        c.addSkill(a, "Firebase Integration", "Authentication, Firestore and push notifications.", "2 weeks");
        c.addSkill(a, "Publishing on Play Store", "Signing, release builds and store listing.", "1 week");
        c.addSkill(a, "Capstone App Project", "Build and publish a complete Android app.", "4 weeks");
        return c;
    }

    private Career createCybersecurityAnalyst() {
        Career c = new Career("Cybersecurity Analyst",
                "Protect computers, networks and data from attacks by monitoring systems, "
                        + "finding vulnerabilities and responding to security incidents.",
                "8 - 10 months", "CYB");
        String b = Skill.BEGINNER, i = Skill.INTERMEDIATE, a = Skill.ADVANCED;
        c.addSkill(b, "Computer Fundamentals", "Hardware, operating systems and how software runs.", "1 week");
        c.addSkill(b, "Networking Basics", "OSI model, TCP/IP, IP addressing, ports and protocols.", "3 weeks");
        c.addSkill(b, "Linux Fundamentals", "Command line, file permissions and users.", "2 weeks");
        c.addSkill(b, "Security Principles", "CIA triad, authentication, authorization and risk.", "1 week");
        c.addSkill(b, "Windows Security Basics", "Active Directory, group policy and event logs.", "1 week");
        c.addSkill(i, "Cryptography Basics", "Hashing, symmetric/asymmetric encryption and certificates.", "2 weeks");
        c.addSkill(i, "Network Security", "Firewalls, VPNs, IDS/IPS and network monitoring.", "2 weeks");
        c.addSkill(i, "Scripting for Security", "Python or Bash scripts to automate security tasks.", "2 weeks");
        c.addSkill(i, "Web Application Security", "OWASP Top 10: SQL injection, XSS and more.", "2 weeks");
        c.addSkill(i, "Vulnerability Assessment", "Scanning systems with tools like Nmap and Nessus.", "2 weeks");
        c.addSkill(a, "SIEM and Log Analysis", "Collecting and analysing logs with tools like Splunk.", "2 weeks");
        c.addSkill(a, "Incident Response", "Detecting, containing and recovering from attacks.", "2 weeks");
        c.addSkill(a, "Ethical Hacking Basics", "Penetration testing methodology in legal lab setups.", "3 weeks");
        c.addSkill(a, "Cloud Security", "Securing cloud accounts, identity and storage.", "2 weeks");
        c.addSkill(a, "Certification Preparation", "Preparing for an entry certification such as CompTIA Security+.", "4 weeks");
        return c;
    }
}
