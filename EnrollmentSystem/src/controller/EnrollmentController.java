package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import model.Student;
import service.EnrollmentService;
import view.EnrollmentMenu;

public class EnrollmentController {
    private final EnrollmentMenu menu;
    private final EnrollmentService service;
    private final Scanner scanner;

    public EnrollmentController(EnrollmentMenu menu, EnrollmentService service) {
        this.menu = menu;
        this.service = service;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean running = true;

        while (running) {
            menu.displayMenu();
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1":
                        addStudentData();
                        break;
                    case "2":
                        displayAllStudents();
                        break;
                    case "3":
                        searchStudent();
                        break;
                    case "4":
                        updateStudentData();
                        break;
                    case "5":
                        deleteStudentData();
                        break;
                    case "6":
                        menu.displayMessage("\nExiting system. Goodbye!");
                        running = false;
                        break;
                    default:
                        menu.displayMessage("\nInvalid choice! Please select 1 to 6.");
                        break;
                }
            } catch (IOException e) {
                menu.displayMessage("\nFile Error: " + e.getMessage());
            } catch (NumberFormatException e) {
                menu.displayMessage("\nInvalid number input! Please enter a valid number.");
            }
        }
    }

    public void addStudentData() throws IOException {
        System.out.println("\n========================================");
        System.out.println("        STUDENT ENROLLMENT SYSTEM       ");
        System.out.println("========================================");
        System.out.print("Enter Student ID: ");
        String id = scanner.nextLine().trim();

        System.out.print("Enter Last Name: ");
        String lastName = scanner.nextLine().trim();

        System.out.print("Enter First Name: ");
        String firstName = scanner.nextLine().trim();

        System.out.print("Enter Middle Name: ");
        String middleName = scanner.nextLine().trim();

        System.out.print("Enter Year Level (1st yr, 2nd yr, 3rd yr, or 4th yr): ");
        String yearLevel = scanner.nextLine().trim();

        System.out.print("Enter Birthday (MM/DD/YYYY): ");
        String birthday = scanner.nextLine().trim();

        String course = promptCourseSelection();

        System.out.print("Enter Age: ");
        int age = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Enter Gender: ");
        String gender = scanner.nextLine().trim();

        System.out.print("Enter Religion: ");
        String religion = scanner.nextLine().trim();

        System.out.print("Enter Home Address: ");
        String homeAddress = scanner.nextLine().trim();

        System.out.print("Enter Citizenship: ");
        String citizenship = scanner.nextLine().trim();

        System.out.print("Enter School Year (e.g. 2026-2027): ");
        String schoolYear = scanner.nextLine().trim();

        System.out.print("Enter Place of Birth: ");
        String placeOfBirth = scanner.nextLine().trim();

        System.out.print("Is Transferee? (Y/N): ");
        String transInput = scanner.nextLine().trim();
        boolean transferee = transInput.equalsIgnoreCase("Y") || transInput.equalsIgnoreCase("YES");

        String lastSchoolName = "";
        String lastSchoolAddress = "";
        if (transferee) {
            System.out.print("Enter Name of Last School: ");
            lastSchoolName = scanner.nextLine().trim();

            System.out.print("Enter Address of Last School: ");
            lastSchoolAddress = scanner.nextLine().trim();
        }

        try {
            service.registerStudent(
                    id, lastName, firstName, middleName,
                    yearLevel, birthday, course, age,
                    gender, religion, homeAddress,
                    citizenship, schoolYear, placeOfBirth,
                    transferee, lastSchoolName, lastSchoolAddress
            );
            menu.displayMessage("\nStudent successfully enrolled!");
        } catch (IllegalArgumentException e) {
            menu.displayMessage("\nEnrollment Failed: " + e.getMessage());
        }
    }

    public void displayAllStudents() throws IOException {
        ArrayList<Student> students = service.getAllStudents();
        menu.displayStudents(students);
    }

    public void searchStudent() throws IOException {
        System.out.print("\nEnter Student ID to search: ");
        String id = scanner.nextLine().trim();

        Student student = service.searchStudentById(id);
        if (student == null) {
            menu.displayMessage("Student with ID '" + id + "' not found.");
        } else {
            System.out.println("\n--- Student Found ---");
            student.displayInfo();
        }
    }

    public void updateStudentData() throws IOException {
        System.out.print("\nEnter Student ID to update: ");
        String id = scanner.nextLine().trim();

        Student existing = service.searchStudentById(id);
        if (existing == null) {
            menu.displayMessage("Student with ID '" + id + "' not found.");
            return;
        }

        System.out.println("\n--- Updating Student (" + id + ") ---");
        System.out.print("Enter New Last Name: ");
        String lastName = scanner.nextLine().trim();

        System.out.print("Enter New First Name: ");
        String firstName = scanner.nextLine().trim();

        System.out.print("Enter New Middle Name: ");
        String middleName = scanner.nextLine().trim();

        System.out.print("Enter New Year Level (1st yr, 2nd yr, 3rd yr, or 4th yr): ");
        String yearLevel = scanner.nextLine().trim();

        System.out.print("Enter New Birthday (MM/DD/YYYY): ");
        String birthday = scanner.nextLine().trim();

        String course = promptCourseSelection();

        System.out.print("Enter New Age: ");
        int age = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Enter New Gender: ");
        String gender = scanner.nextLine().trim();

        System.out.print("Enter New Religion: ");
        String religion = scanner.nextLine().trim();

        System.out.print("Enter New Home Address: ");
        String homeAddress = scanner.nextLine().trim();

        System.out.print("Enter New Citizenship: ");
        String citizenship = scanner.nextLine().trim();

        System.out.print("Enter New School Year (e.g. 2026-2027): ");
        String schoolYear = scanner.nextLine().trim();

        System.out.print("Enter New Place of Birth: ");
        String placeOfBirth = scanner.nextLine().trim();

        System.out.print("Is Transferee? (Y/N): ");
        String transInput = scanner.nextLine().trim();
        boolean transferee = transInput.equalsIgnoreCase("Y") || transInput.equalsIgnoreCase("YES");

        String lastSchoolName = "";
        String lastSchoolAddress = "";
        if (transferee) {
            System.out.print("Enter Name of Last School: ");
            lastSchoolName = scanner.nextLine().trim();

            System.out.print("Enter Address of Last School: ");
            lastSchoolAddress = scanner.nextLine().trim();
        }

        try {
            service.updateStudent(
                    id, lastName, firstName, middleName,
                    yearLevel, birthday, course, age,
                    gender, religion, homeAddress,
                    citizenship, schoolYear, placeOfBirth,
                    transferee, lastSchoolName, lastSchoolAddress
            );
            menu.displayMessage("\nStudent record updated successfully!");
        } catch (IllegalArgumentException e) {
            menu.displayMessage("\nUpdate Failed: " + e.getMessage());
        }
    }

    public void deleteStudentData() throws IOException {
        System.out.print("\nEnter Student ID to delete: ");
        String id = scanner.nextLine().trim();

        Student existing = service.searchStudentById(id);
        if (existing == null) {
            menu.displayMessage("Student with ID '" + id + "' not found.");
            return;
        }

        System.out.print("Are you sure you want to delete " + existing.getFirstName() + " " + existing.getLastName() + " (" + id + ")? (Y/N): ");
        String confirm = scanner.nextLine().trim();

        if (confirm.equalsIgnoreCase("Y") || confirm.equalsIgnoreCase("YES")) {
            boolean deleted = service.deleteStudent(id);
            if (deleted) {
                menu.displayMessage("\nStudent record deleted successfully!");
            } else {
                menu.displayMessage("\nFailed to delete student record.");
            }
        } else {
            menu.displayMessage("\nDelete cancelled.");
        }
    }

    private String promptCourseSelection() {
        System.out.println("\nSelect Course:");
        System.out.println("[1] BSIT - Bachelor of Science in Information Technology");
        System.out.println("[2] BSCS - Bachelor of Science in Computer Science");
        System.out.println("[3] BSIS - Bachelor of Science in Information Systems");
        System.out.println("[4] ACT  - Associate in Computer Technology");
        System.out.print("Enter choice (1-4): ");
        String choice = scanner.nextLine().trim();

        return switch (choice) {
            case "1" -> "BSIT";
            case "2" -> "BSCS";
            case "3" -> "BSIS";
            case "4" -> "ACT";
            default -> {
                System.out.println("Invalid choice! Defaulting course to Undeclared.");
                yield "Undeclared";
            }
        };
    }
}