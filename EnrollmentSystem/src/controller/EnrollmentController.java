package controller;

import java.util.ArrayList;
import java.util.List;
import model.*;
import service.EnrollmentService;
import view.EnrollmentMenu;

public class EnrollmentController {
    private EnrollmentMenu menu;
    private EnrollmentService service;

    // Temporary in-memory lists for new model classes
    private List<Subject> subjects = new ArrayList<>();
    private List<EnrollmentRecord> enrollmentRecords = new ArrayList<>();
    private List<Payment> payments = new ArrayList<>();
    private List<Shifting> shiftings = new ArrayList<>();
    private List<User> users = new ArrayList<>();

    public EnrollmentController(EnrollmentMenu menu, EnrollmentService service) {
        this.menu = menu;
        this.service = service;
    }

    public void start() {
        boolean running = true;
        while (running) {
            menu.displayMenu();
            int choice = menu.getChoice();

            switch (choice) {
                case 1:
                    // I-pabilin o i-tawag ang imong existing register student logic diri
                    System.out.println("[Register Student]");
                    break;
                case 2:
                    System.out.println("[Display All Student]");
                    break;
                case 3:
                    System.out.println("[Search Student]");
                    break;
                case 4:
                    System.out.println("[Update Student]");
                    break;
                case 5:
                    System.out.println("[Delete Student]");
                    break;
                case 6:
                    addSubject();
                    break;
                case 7:
                    addEnrollmentRecord();
                    break;
                case 8:
                    addPayment();
                    break;
                case 9:
                    addShifting();
                    break;
                case 10:
                    addUser();
                    break;
                case 0:
                    running = false;
                    System.out.println("Exiting system...");
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void addSubject() {
        String code = menu.readLine("Subject Code: ");
        String title = menu.readLine("Subject Title: ");
        int units = Integer.parseInt(menu.readLine("Units: "));
        subjects.add(new Subject(code, title, units));
        System.out.println("Subject added! Total subjects: " + subjects.size());
    }

    private void addEnrollmentRecord() {
        String id = menu.readLine("Enrollment ID: ");
        String studentId = menu.readLine("Student ID: ");
        String subCode = menu.readLine("Subject Code: ");
        String sem = menu.readLine("Semester: ");
        String status = menu.readLine("Status: ");
        enrollmentRecords.add(new EnrollmentRecord(id, studentId, subCode, sem, status));
        System.out.println("Enrollment record saved! Total: " + enrollmentRecords.size());
    }

    private void addPayment() {
        String payId = menu.readLine("Payment ID: ");
        String studentId = menu.readLine("Student ID: ");
        double amount = Double.parseDouble(menu.readLine("Amount: "));
        String date = menu.readLine("Date: ");
        String method = menu.readLine("Method: ");
        payments.add(new Payment(payId, studentId, amount, date, method));
        System.out.println("Payment recorded! Total payments: " + payments.size());
    }

    private void addShifting() {
        String shiftId = menu.readLine("Shifting ID: ");
        String studentId = menu.readLine("Student ID: ");
        String fromCourse = menu.readLine("From Course: ");
        String toCourse = menu.readLine("To Course: ");
        String status = menu.readLine("Status: ");
        shiftings.add(new Shifting(shiftId, studentId, fromCourse, toCourse, status));
        System.out.println("Shifting request added! Total: " + shiftings.size());
    }

    private void addUser() {
        String username = menu.readLine("Username: ");
        String password = menu.readLine("Password: ");
        String fullName = menu.readLine("Full Name: ");
        String role = menu.readLine("Role: ");
        users.add(new User(username, password, fullName, role));
        System.out.println("User added! Total users: " + users.size());
    }
}