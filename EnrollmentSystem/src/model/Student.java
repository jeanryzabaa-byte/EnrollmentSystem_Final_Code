package model;

import controller.EnrollmentController;
import service.EnrollmentService;
import util.InputValidator;
import view.ConsoleView;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            EnrollmentService service = new EnrollmentService();
            ConsoleView view = new ConsoleView();
            InputValidator input = new InputValidator(scanner);
            EnrollmentController controller = new EnrollmentController(service, view, input);
            controller.run();
        }
    }
}

public class Student {
    private final String studentNumber;
    private final String fullName;
    private String course;
    private int yearLevel;
    private String status; // ACTIVE, INACTIVE, SHIFTED

    public Student(String studentNumber, String fullName, String course, int yearLevel, String status) {
        this.studentNumber = studentNumber;
        this.fullName = fullName;
        this.course = course;
        this.yearLevel = yearLevel;
        this.status = status;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public int getYearLevel() {
        return yearLevel;
    }

    public void setYearLevel(int yearLevel) {
        this.yearLevel = yearLevel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String toDataString() {
        return escape(studentNumber) + "|" + escape(fullName) + "|" + escape(course) + "|" + yearLevel + "|" + escape(status);
    }

    public static Student fromDataString(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 5) return null;
        try {
            int yr = Integer.parseInt(parts[3].trim());
            return new Student(unescape(parts[0]), unescape(parts[1]), unescape(parts[2]), yr, unescape(parts[4]));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | %s - Year %d | Status: %s",
                studentNumber, fullName, course, yearLevel, status);
    }

    private static String escape(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("|", "\\p");
    }

    private static String unescape(String s) {
        return s == null ? "" : s.replace("\\p", "|").replace("\\\\", "\\");
    }
}