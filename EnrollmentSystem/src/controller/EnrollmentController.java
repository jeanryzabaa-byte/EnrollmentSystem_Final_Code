package controller;

import model.Student;
import model.User;
import service.EnrollmentService;
import util.InputValidator;
import view.ConsoleView;

public class EnrollmentController {
    private final EnrollmentService service;
    private final ConsoleView view;
    private final InputValidator input;

    public EnrollmentController(EnrollmentService service, ConsoleView view, InputValidator input) {
        this.service = service;
        this.view = view;
        this.input = input;
    }

    public void run() {
        view.showBanner();
        while (true) {
            view.showMainMenu();
            String choice = input.readLine("Choice: ");
            if (choice.equals("2")) {
                view.showMessage("Goodbye!");
                break;
            }
            if (!choice.equals("1")) {
                view.showMessage("Invalid option.");
                continue;
            }

            String username = input.readNonEmpty("Username: ");
            String password = input.readNonEmpty("Password: ");
            User loggedIn = service.login(username, password);

            if (loggedIn == null) {
                view.showMessage("Invalid username or password.");
                continue;
            }

            view.showMessage("\nWelcome, " + loggedIn.getUsername() + " [" + loggedIn.getRole() + "]");
            routeByRole(loggedIn);
        }
    }

    private void routeByRole(User user) {
        String role = user.getRole() == null ? "" : user.getRole().trim().toUpperCase();
        switch (role) {
            case "ADMIN", "SYSTEM ADMINISTRATOR" -> handleAdmin();
            case "REGISTRAR" -> handleRegistrar();
            case "CASHIER" -> handleCashier();
            case "STUDENT" -> handleStudent(user);
            case "INSTRUCTOR" -> handleInstructor(user);
            default -> view.showMessage("Unknown role: " + user.getRole());
        }
    }

    private void handleAdmin() {
        while (true) {
            view.showAdminMenu();
            String c = input.readLine("Choice: ");
            switch (c) {
                case "1" -> {
                    String u = input.readNonEmpty("New Username: ");
                    String p = input.readNonEmpty("Password: ");
                    String r = input.readNonEmpty("Role (ADMIN/REGISTRAR/CASHIER/STUDENT/INSTRUCTOR): ");
                    String lid = input.readNonEmpty("Linked ID (e.g. 2026-0001 or staff ID): ");
                    view.showMessage(service.addUser(u, p, r, lid) ? "User account created." : "Failed: duplicate or invalid role.");
                }
                case "2" -> view.printUsers(service.getUsers());
                case "3" -> handleAddSubject();
                case "4" -> view.printSubjects(service.getSubjects());
                case "5" -> view.printStudents(service.getStudents());
                case "0" -> {
                    return;
                }
                default -> view.showMessage("Invalid choice.");
            }
        }
    }

    private void handleRegistrar() {
        while (true) {
            view.showRegistrarMenu();
            String c = input.readLine("Choice: ");
            switch (c) {
                case "1" -> {
                    String sn = input.readNonEmpty("Student Number (e.g. 2026-0001): ");
                    String name = input.readNonEmpty("Full Name: ");
                    String course = input.readNonEmpty("Course (e.g. BSIT): ");
                    int yr = input.readInt("Year Level (1-5): ", 1, 5);
                    view.showMessage(service.addStudent(sn, name, course, yr)
                            ? "Student record added."
                            : "Student number already exists.");
                }
                case "2" -> view.printStudents(service.getStudents());
                case "3" -> {
                    String sn = input.readNonEmpty("Student Number: ");
                    view.printSubjects(service.getSubjects());
                    String sc = input.readNonEmpty("Subject Code: ");
                    String sem = input.readNonEmpty("Semester (e.g. 1st Sem 2026-2027): ");
                    view.showMessage(service.enrollStudent(sn, sc, sem));
                }
                case "4" -> {
                    String sn = input.readNonEmpty("Student Number: ");
                    view.printEnrollments(service.getEnrollmentsByStudent(sn));
                }
                case "5" -> handleAddSubject();
                case "6" -> {
                    var reqs = service.getShiftingRequests();
                    view.printShiftingRequests(reqs);
                    if (!reqs.isEmpty()) {
                        String reqId = input.readLine("Enter Request ID to process (or Enter to skip): ");
                        if (!reqId.isEmpty()) {
                            boolean approve = input.readLine("Approve request? (y/n): ").equalsIgnoreCase("y");
                            view.showMessage(service.processShiftingRequest(reqId, approve)
                                    ? "Shifting request updated."
                                    : "Request ID not found.");
                        }
                    }
                }
                case "0" -> {
                    return;
                }
                default -> view.showMessage("Invalid choice.");
            }
        }
    }

    private void handleCashier() {
        while (true) {
            view.showCashierMenu();
            String c = input.readLine("Choice: ");
            switch (c) {
                case "1" -> {
                    String sn = input.readNonEmpty("Student Number: ");
                    Student st = service.findStudent(sn);
                    if (st == null) {
                        view.showMessage("Student not found.");
                    } else {
                        view.printBillingSummary(
                                st,
                                service.calculateTotalAssessment(sn),
                                service.calculateTotalPaid(sn),
                                service.calculateBalance(sn)
                        );
                    }
                }
                case "2" -> {
                    String sn = input.readNonEmpty("Student Number: ");
                    double amt = input.readPositiveDouble("Amount: ");
                    String method = input.readNonEmpty("Payment Method (CASH / GCASH / BANK / CARD): ");
                    view.showMessage(service.recordPayment(sn, amt, method));
                }
                case "3" -> {
                    String sn = input.readNonEmpty("Student Number: ");
                    view.printPayments(service.getPaymentsByStudent(sn));
                }
                case "0" -> {
                    return;
                }
                default -> view.showMessage("Invalid choice.");
            }
        }
    }

    private void handleStudent(User user) {
        String studNo = user.getLinkedId();
        while (true) {
            view.showStudentMenu(studNo);
            String c = input.readLine("Choice: ");
            switch (c) {
                case "1" -> {
                    Student st = service.findStudent(studNo);
                    if (st == null) {
                        view.showMessage("No student record found for '" + studNo + "'. Ask Registrar to add it first.");
                    } else {
                        view.showMessage(st.toString());
                    }
                }
                case "2" -> view.printEnrollments(service.getEnrollmentsByStudent(studNo));
                case "3" -> {
                    Student st = service.findStudent(studNo);
                    if (st == null) {
                        view.showMessage("No student record found for '" + studNo + "'.");
                    } else {
                        view.printBillingSummary(
                                st,
                                service.calculateTotalAssessment(studNo),
                                service.calculateTotalPaid(studNo),
                                service.calculateBalance(studNo)
                        );
                    }
                }
                case "4" -> {
                    String target = input.readNonEmpty("Target Course (e.g. BSCS): ");
                    String reason = input.readNonEmpty("Reason for shifting: ");
                    view.showMessage(service.submitShiftingRequest(studNo, target, reason));
                }
                case "5" -> view.printShiftingRequests(service.getShiftingRequestsByStudent(studNo));
                case "0" -> {
                    return;
                }
                default -> view.showMessage("Invalid choice.");
            }
        }
    }

    private void handleInstructor(User user) {
        String instId = user.getLinkedId();
        while (true) {
            view.showInstructorMenu(instId);
            String c = input.readLine("Choice: ");
            switch (c) {
                case "1" -> view.printSubjects(service.getSubjectsForInstructor(instId));
                case "2" -> {
                    String code = input.readNonEmpty("Subject Code: ");
                    var roster = service.getEnrollmentsBySubject(code);
                    view.printEnrollments(roster);
                    if (!roster.isEmpty()) {
                        String recId = input.readLine("Enter Enrollment Record ID to grade (or Enter to skip): ");
                        if (!recId.isEmpty()) {
                            String grade = input.readNonEmpty("Grade (e.g. 1.25, 1.50, INC): ");
                            view.showMessage(service.updateGrade(recId, grade) ? "Grade encoded." : "Record ID not found.");
                        }
                    }
                }
                case "0" -> {
                    return;
                }
                default -> view.showMessage("Invalid choice.");
            }
        }
    }

    private void handleAddSubject() {
        String code = input.readNonEmpty("Subject Code: ");
        String title = input.readNonEmpty("Subject Title: ");
        int units = input.readInt("Units (1-10): ", 1, 10);
        double fee = input.readPositiveDouble("Fee per Unit: ");
        String inst = input.readLine("Instructor Username (optional, e.g. instructor): ");
        view.showMessage(service.addSubject(code, title, units, fee, inst)
                ? "Subject added."
                : "Subject code already exists.");
    }
}