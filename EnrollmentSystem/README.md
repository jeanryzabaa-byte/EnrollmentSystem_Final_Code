# Enrollment Management System

A Java console-based student enrollment application using OOP, layered organization, MVC concepts, and the Repository Pattern.

## Requirements
- Java JDK installed
- Visual Studio Code (or another Java IDE)

No XAMPP, MySQL, or PHP is required.

## Project structure
- `src/model`: Enrollment and Student classes
- `src/view`: Console menu and output
- `src/controller`: Handles menu choices
- `src/service`: Enrollment operations
- `src/repository`: Student file-storage interface and implementation
- `src/util`: Input validation
- `data/students.txt`: Saved student records

## Compile (run from the EnrollmentSystem folder)

Windows PowerShell:
```powershell
javac -d out src/Main.java src/model/*.java src/view/*.java src/controller/*.java src/service/*.java src/repository/*.java src/util/*.java
```

## Run
```powershell
java -cp out Main
```

The program creates `data/students.txt` automatically if it does not exist.
