package controller;

import java.io.IOException;
import java.util.Scanner;
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
        try {
            addStudentData();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void addStudentData() throws IOException {
        System.out.print("Enter Student ID: ");
        String id = scanner.nextLine();

        System.out.print("Enter Last Name: ");
        String lastName = scanner.nextLine();

        System.out.print("Enter First Name: ");
        String firstName = scanner.nextLine();

        System.out.print("Enter Middle Name: ");
        String middleName = scanner.nextLine();

        System.out.print("Enter Year Level (1st yr, 2nd yr, 3rd yr, or 4th yr): ");
        String yearLevel = scanner.nextLine();

        System.out.print("Enter Birthday (MM/DD/YYYY): ");
        String birthday = scanner.nextLine();

        System.out.println("\nSelect Course:");
        System.out.println("[1] BSIT - Bachelor of Science in Information Technology");
        System.out.println("[2] BSCS - Bachelor of Science in Computer Science");
        System.out.println("[3] BSIS - Bachelor of Science in Information Systems");
        System.out.println("[4] ACT  - Associate in Computer Technology");
        System.out.print("Enter choice (1-4): ");
        String choice = scanner.nextLine();

        String course = "";
        switch (choice) {
            case "1":
                course = "BSIT";
                break;
            case "2":
                course = "BSCS";
                break;
            case "3":
                course = "BSIS";
                break;
            case "4":
                course = "ACT";
                break;
            default:
                System.out.println("Invalid choice! Defaulting course to Undeclared.");
                course = "Undeclared";
                break;
        }

        try {
            service.registerStudent(id, lastName, firstName, middleName, yearLevel, birthday, course);
            System.out.println("\nStudent successfully enrolled!");
        } catch (IllegalArgumentException e) {
            System.out.println("\nEnrollment Failed: " + e.getMessage());
        }
    }
}