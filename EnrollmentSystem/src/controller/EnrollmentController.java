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
            if (choice.equals("3")) {
                view.showMessage("Goodbye! Thank you for using the system.");
                break;
            }
            if (choice.equals("2")) {
                handleRegisterAccount();
                continue;
            }
            if (!choice.equals("1")) {
                view.showMessage("Invalid option. Please choose 1, 2, or 3.");
                continue;
            }

            String username = input.readNonEmpty("Username: ");
            String password = input.readNonEmpty("Password: ");
            User loggedIn = service.login(username, password);

            if (loggedIn == null) {
                view.showMessage("Invalid username or password. Choose 2 to register an account first.");
                continue;
            }

            view.showMessage("\n====================================================");
            view.showMessage("Welcome, " + loggedIn.getUsername() + " [" + loggedIn.getRole() + "]");
            view.showMessage("====================================================");
            routeByRole(loggedIn);
        }
    }

    private void handleRegisterAccount() {
        view.showMessage("\n--- STEP 1: REGISTER ACCOUNT ---");
        String u = input.readNonEmpty("Set Username: ");
        String p = input.readNonEmpty("Set Password: ");
        view.showMessage("Roles: ADMIN | REGISTRAR | CASHIER | STUDENT | INSTRUCTOR");
        String r = input.readNonEmpty("Enter Role: ");
        String lid = input.readNonEmpty("Linked ID (e.g. 25-10453 or STAFF-01): ");
        boolean created = service.addUser(u, p, r, lid);
        view.showMessage(created
                ? "Account created! Choose 1 to Login."
                : "Failed: username already exists or invalid role.");
    }

    private void routeByRole(User user) {
        String role = user.getRole() == null ? "" : user.getRole().trim().toUpperCase();
        if (role.contains("ADMIN")) {
            handleAdmin();
        } else if (role.contains("REGISTRAR")) {
            handleRegistrar();
        } else if (role.contains("CASHIER")) {
            handleCashier();
        } else if (role.contains("STUDENT")) {
            handleStudent(user);
        } else if (role.contains("INSTRUCTOR")) {
            handleInstructor(user);
        } else {
            view.showMessage("Unknown role: " + user.getRole());
        }
    }

    private void handleAdmin() {
        while (true) {
            view.showAdminMenu();
            String c = input.readLine("Choice: ");
            switch (c) {
                case "1" -> {
                    view.showMessage("\n[Admin Step 1] Create System Account");
                    String u = input.readNonEmpty("New Username: ");
                    String p = input.readNonEmpty("Password: ");
                    String r = input.readNonEmpty("Role (ADMIN / REGISTRAR / CASHIER / STUDENT / INSTRUCTOR): ");
                    String lid = input.readNonEmpty("Linked ID: ");
                    view.showMessage(service.addUser(u, p, r, lid)
                            ? "User account created successfully."
                            : "Failed: duplicate username or invalid role.");
                }
                case "2" -> {
                    view.showMessage("\n[Admin Step 2] System User Accounts");
                    view.printUsers(service.getUsers());
                }
                case "3" -> handleAddSubject();
                case "4" -> {
                    view.showMessage("\n[Admin Step 4] Subject Catalog");
                    view.printSubjects(service.getSubjects());
                }
                case "5" -> {
                    view.showMessage("\n[Admin Step 5] Master Student Directory");
                    view.printStudents(service.getStudents());
                }
                case "0" -> {
                    view.showMessage("Logging out from Admin...");
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
                    view.showMessage("\n[Registrar Step 1] Enroll Regular Student");
                    String sn = input.readNonEmpty("Student No. (e.g. 25-10453): ");
                    String name = input.readNonEmpty("Full Name: ");
                    String course = input.readNonEmpty("Course (e.g. BSIT): ");
                    int yr = input.readInt("Year Level (1-5): ", 1, 5);
                    view.showMessage(service.addStudentWithType(sn, name, course, yr, "REGULAR")
                            ? "Regular student registered."
                            : "Student number already exists.");
                }
                case "2" -> {
                    view.showMessage("\n[Registrar Step 2] Enroll Transferee Student");
                    String sn = input.readNonEmpty("Transferee Student No.: ");
                    String name = input.readNonEmpty("Full Name: ");
                    String prevSchool = input.readNonEmpty("Previous School: ");
                    String course = input.readNonEmpty("Target Course (e.g. BSIT): ");
                    int yr = input.readInt("Credited Year Level (1-5): ", 1, 5);
                    boolean ok = service.addStudentWithType(sn, name + " [From: " + prevSchool + "]", course, yr, "TRANSFEREE");
                    view.showMessage(ok ? "Transferee student registered." : "Student number already exists.");
                }
                case "3" -> {
                    view.showMessage("\n[Registrar Step 3] Registered Students");
                    view.printStudents(service.getStudents());
                }
                case "4" -> {
                    view.showMessage("\n[Registrar Step 4] Enroll Student in Subject");
                    String sn = input.readNonEmpty("Student No.: ");
                    view.printSubjects(service.getSubjects());
                    String sc = input.readNonEmpty("Subject Code: ");
                    String sem = input.readNonEmpty("Academic Period / Semester (e.g. 2026-2027 1st): ");
                    view.showMessage(service.enrollStudent(sn, sc, sem));
                }
                case "5" -> {
                    view.showMessage("\n[Registrar Step 5] Student Official Registration / Enrolled Subjects");
                    String sn = input.readNonEmpty("Student No.: ");
                    Student st = service.findStudent(sn);
                    if (st != null) view.showMessage("Student: " + st);
                    view.printEnrollments(service.getEnrollmentsByStudent(sn));
                }
                case "6" -> handleAddSubject();
                case "7" -> {
                    view.showMessage("\n[Registrar Step 7] Shifting Requests");
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
                    view.showMessage("Logging out from Registrar...");
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
                    view.showMessage("\n[Cashier Step 1] Student Assessment Slip & Tuition Balance");
                    String sn = input.readNonEmpty("Student No.: ");
                    Student st = service.findStudent(sn);
                    if (st == null) view.showMessage("Student not found.");
                    else showBilling(st, sn);
                }
                case "2" -> {
                    view.showMessage("\n[Cashier Step 2] Set / Update Laboratory & General/Miscellaneous Fees");
                    String sn = input.readNonEmpty("Student No.: ");
                    if (service.findStudent(sn) == null) {
                        view.showMessage("Student not found.");
                    } else {
                        double lab = input.readPositiveDouble("Laboratory Fee Total: ");
                        double misc = input.readPositiveDouble("General / Miscellaneous Fee Total: ");
                        service.setAdditionalFees(sn, lab, misc);
                        view.showMessage("Laboratory and Miscellaneous fees updated.");
                    }
                }
                case "3" -> {
                    view.showMessage("\n[Cashier Step 3] Apply Discount / Scholarship / Fee Waiver");
                    String sn = input.readNonEmpty("Student No.: ");
                    if (service.findStudent(sn) == null) {
                        view.showMessage("Student not found.");
                    } else {
                        double waive = input.readPositiveDouble("Discount / Waived Amount: ");
                        service.waiveFee(sn, waive);
                        view.showMessage("Discount / waiver applied.");
                    }
                }
                case "4" -> {
                    view.showMessage("\n[Cashier Step 4] Record Tuition Payment");
                    String sn = input.readNonEmpty("Student No.: ");
                    double amt = input.readPositiveDouble("Payment Amount: ");
                    String method = input.readNonEmpty("Payment Method (CASH / GCASH / BANK / CARD): ");
                    view.showMessage(service.recordPayment(sn, amt, method));
                }
                case "5" -> {
                    view.showMessage("\n[Cashier Step 5] Student Payment History");
                    String sn = input.readNonEmpty("Student No.: ");
                    view.printPayments(service.getPaymentsByStudent(sn));
                }
                case "0" -> {
                    view.showMessage("Logging out from Cashiering...");
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
                    if (st == null) view.showMessage("No student record found for '" + studNo + "'. Ask Registrar to register it first.");
                    else view.showMessage(st.toString());
                }
                case "2" -> view.printEnrollments(service.getEnrollmentsByStudent(studNo));
                case "3" -> {
                    Student st = service.findStudent(studNo);
                    if (st == null) view.showMessage("No student record found for '" + studNo + "'.");
                    else showBilling(st, studNo);
                }
                case "4" -> {
                    String target = input.readNonEmpty("Target Course (e.g. BSCS): ");
                    String reason = input.readNonEmpty("Reason for shifting: ");
                    view.showMessage(service.submitShiftingRequest(studNo, target, reason));
                }
                case "5" -> view.printShiftingRequests(service.getShiftingRequestsByStudent(studNo));
                case "0" -> {
                    view.showMessage("Logging out...");
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
                            String grade = input.readNonEmpty("Grade (e.g. 1.00, 1.25, INC): ");
                            view.showMessage(service.updateGrade(recId, grade) ? "Grade encoded." : "Record ID not found.");
                        }
                    }
                }
                case "0" -> {
                    view.showMessage("Logging out...");
                    return;
                }
                default -> view.showMessage("Invalid choice.");
            }
        }
    }

    private void handleAddSubject() {
        view.showMessage("\n[Subject Setup] Add Subject");
        String code = input.readNonEmpty("Subject Code (e.g. IT 205): ");
        String title = input.readNonEmpty("Subject Title: ");
        int units = input.readInt("Units (1-10): ", 1, 10);
        double fee = input.readPositiveDouble("Tuition Rate per Unit (e.g. 150.00): ");
        String inst = input.readLine("Instructor Username (optional): ");
        view.showMessage(service.addSubject(code, title, units, fee, inst)
                ? "Subject added."
                : "Subject code already exists.");
    }

    private void showBilling(Student st, String sn) {
        double tuition = service.calculateTotalAssessment(sn);
        double lab = service.getLabFee(sn);
        double misc = service.getMiscFee(sn);
        double waived = service.getWaivedAmount(sn);
        double totalAssessment = Math.max(0.0, tuition + lab + misc - waived);
        double paid = service.calculateTotalPaid(sn);
        double balance = Math.max(0.0, totalAssessment - paid);
        view.printDetailedBilling(st, tuition, lab, misc, waived, totalAssessment, paid, balance);
    }
}