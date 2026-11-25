package controller;

import java.util.List;
import java.util.Scanner;
import model.Student;
import service.EnrollmentService;
import view.ConsoleView;

public class EnrollmentController {
    private final EnrollmentService enrollmentService;
    private final ConsoleView consoleView;
    private final Scanner scanner;

    public EnrollmentController(EnrollmentService enrollmentService, ConsoleView consoleView) {
        this.enrollmentService = enrollmentService;
        this.consoleView = consoleView;
        this.scanner = new Scanner(System.in);
    }

    public void viewAllStudents() {
        List<Student> list = enrollmentService.getAllStudentsWithStatus();
        consoleView.printStudentEnrollments(list);
    }

    public void viewStudentEnrollments() {
        List<Student> list = enrollmentService.getAllStudentsWithStatus();
        consoleView.printStudentEnrollments(list);
    }

    public void pause() {
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }
}