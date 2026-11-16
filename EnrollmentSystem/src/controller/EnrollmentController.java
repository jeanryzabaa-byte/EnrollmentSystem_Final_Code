package controller;

import java.io.IOException;
import java.util.Scanner;
import model.Student;
import service.EnrollmentService;
import view.EnrollmentMenu;

public class EnrollmentController {
    private EnrollmentMenu menu;
    private EnrollmentService service;
    private Scanner scanner;

    public EnrollmentController(EnrollmentMenu menu, EnrollmentService service) {
        this.menu = menu;
        this.service = service;
        this.scanner = new Scanner(System.in);
    }

    public void start() {


    }

    public void addStudentData() throws IOException {
        System.out.print("Enter Student ID: ");
        String id = scanner.nextLine();

        System.out.print("Enter Full Name: ");
        String name = scanner.nextLine();

        // 1. Year Level
        System.out.print("Enter Year Level (1, 2, 3, or 4): ");
        int yearLevel = scanner.nextInt();
        scanner.nextLine(); // Clear buffer

        System.out.print("Enter Birthday (MM/DD/YYYY): ");
        String birthday = scanner.nextLine();

        System.out.println("\nSelect Course:");
        System.out.println("[1] BSIT - Bachelor of Science in Information Technology");
        System.out.println("[2] BSCS - Bachelor of Science in Computer Science");
        System.out.println("[3] BSIS - Bachelor of Science in Information Systems");
        System.out.println("[4] ACT  - Associate in Computer Technology");
        System.out.print("Enter choice (1-4): ");
        int courseChoice = scanner.nextInt();
        scanner.nextLine(); // Clear buffer

        String course = ",";
        switch (courseChoice) {
            case 1:
                course = "BSIT";
                break;
            case 2:
                course = "BSCS";
                break;
            case 3:
                course = "BSIS";
                break;
            case 4:
                course = "ACT";
                break;
            default:
                course = "Undecided";
                System.out.println("Invalid choice, set to Undecided.");
                break;
        }

        Student newStudent = new Student(id, name, yearLevel, birthday, course);
        service.registerStudent(id, name, yearLevel, birthday, course);
        System.out.println("Student successfully enrolled!");
    }
}