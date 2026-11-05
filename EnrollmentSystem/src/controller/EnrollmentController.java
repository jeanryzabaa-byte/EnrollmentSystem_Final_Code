package controller;

import view.EnrollmentMenu;
import service.EnrollmentService;
import util.InputValidator;
import java.io.IOException;

public class EnrollmentController {
    private final EnrollmentMenu menu;
    private final EnrollmentService service;

    public EnrollmentController(EnrollmentMenu menu,
                                EnrollmentService service) {
        this.menu = menu;
        this.service = service;
    }

    public void start() {
        boolean running = true;

        while (running) {
            menu.displayMenu();
            String choice = menu.getInput("");

            switch (choice) {
                case "1":
                    addStudent();
                    break;
                case "2":
                    viewRecords();
                    break;
                case "3":
                    menu.displayMessage(
                            "Thank you for using Enrollment System!");
                    running = false;
                    break;
                default:
                    menu.displayMessage("Invalid choice! Please try again.");
            }
        }
    }

    private void addStudent() {
        try {
            String id = menu.getInput("Enter Student ID: ");
            String name = menu.getInput("Enter Student Name: ");
            String course = menu.getInput("Enter Course: ");

            if (!InputValidator.isValid(id)
                    || !InputValidator.isValid(name)
                    || !InputValidator.isValid(course)) {
                menu.displayMessage(
                        "Invalid input! Fields cannot be empty or contain |.");
                return;
            }

            service.registerStudent(name, id, course);
            menu.displayMessage("Record saved successfully!");
        } catch (IllegalArgumentException e) {
            menu.displayMessage(e.getMessage());
        } catch (IOException e) {
            menu.displayMessage("Error saving record: " + e.getMessage());
        }
    }

    private void viewRecords() {
        try {
            menu.displayStudents(service.viewStudents());
        } catch (IOException e) {
            menu.displayMessage("Error reading records: " + e.getMessage());
        }
    }
}
