package controller;

import model.Student;
import model.Subject;
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
                view.showMessage("Goodbye! Thank you for using the system.");
                break;
            }
            if (!choice.equals("1")) {
                view.showMessage("Invalid option. Please choose 1 or 2.");
                continue;
            }
            User loggedIn = service.login(input.readNonEmpty("Username: "), input.readPassword("Password: "));
            if (loggedIn == null) {
                view.showMessage("Invalid username or password.");
                continue;
            }
            view.showMessage("\n====================================================\nWelcome, "
                    + loggedIn.getUsername() + " [" + loggedIn.getRole() + "]\n====================================================");
            routeByRole(loggedIn);
        }
    }

    private void routeByRole(User user) {
        String r = user.getRole() == null ? "" : user.getRole().trim().toUpperCase();
        if (r.contains("ADMIN")) handleAdmin();
        else if (r.contains("REGISTRAR")) handleRegistrar();
        else if (r.contains("CASHIER")) handleCashier(null);
        else if (r.contains("STUDENT")) handleStudent(user);
        else if (r.contains("INSTRUCTOR")) handleInstructor(user);
        else view.showMessage("Unknown role: " + user.getRole());
    }

    private void handleAdmin() {
        while (true) {
            view.showAdminMenu();
            switch (input.readLine("Choice: ")) {
                case "1" -> {
                    view.showMessage("\n[Admin Step 1] Add User Account");
                    String role = input.readNonEmpty("Role (CASHIER / REGISTRAR): ");
                    if (role.trim().equalsIgnoreCase("STUDENT")) {
                        view.showMessage("STUDENT role is not allowed in Add User Account.");
                        break;
                    }
                    String lid = input.readNonEmpty("Linked ID: ");
                    String u = input.readNonEmpty("Set Username: ");
                    String p = input.readPassword("Set Password: ");
                    view.showMessage(service.addUser(u, p, role, lid)
                            ? "User account added successfully." : "Failed: duplicate username or invalid role.");
                }
                case "2" -> {
                    view.showMessage("\n[Admin Step 2] Added User Accounts");
                    view.printUsers(service.getAddedUsersOnly());
                }
                case "3" -> {
                    view.showMessage("\n[Admin Step 3] Subjects & Assigned Instructors");
                    for (Subject s : service.getSubjects()) {
                        String inst = (s.getInstructor() == null || s.getInstructor().isBlank()) ? "Unassigned" : s.getInstructor();
                        view.showMessage("  * " + s.getCode() + " - " + s.getTitle() + " (" + s.getUnits() + " units) | Instructor: " + inst);
                    }
                }
                case "4" -> {
                    view.showMessage("\n[Admin Step 4] Master Enrolled Students & Assigned Instructors");
                    service.getAdminEnrollmentAndInstructorReport().forEach(l -> view.showMessage("  * " + l));
                    if (!service.getAdminSyncedRecords().isEmpty()) {
                        view.showMessage("\n--- Synced Billing & Portal Summary ---");
                        service.getAdminSyncedRecords().forEach(rec -> view.showMessage("  - " + rec));
                    }
                }
                case "0" -> { view.showMessage("Logging out from Admin..."); return; }
                default -> view.showMessage("Invalid choice.");
            }
        }
    }

    private void handleRegistrar() {
        while (true) {
            view.showRegistrarMenu();
            switch (input.readLine("Choice: ")) {
                case "1" -> enrollStudentFlow("REGULAR", false);
                case "2" -> enrollStudentFlow("TRANSFEREE", true);
                case "3" -> {
                    view.showMessage("\n[Registrar Step 3] Registered Students");
                    view.printStudents(service.getStudents());
                }
                case "4" -> {
                    view.showMessage("\n[Registrar Step 4] Student Enrollments");
                    String sn = input.readLine("Student No. (press Enter to display ALL): ");
                    service.getRegistrarEnrollmentSummary(sn).forEach(l -> view.showMessage("  " + l));
                }
                case "5" -> handleAddSubject();
                case "6" -> {
                    view.showMessage("\n[Registrar Step 6] All Subjects");
                    var subs = service.getSubjects();
                    if (subs.isEmpty()) view.showMessage("No subjects found.");
                    else subs.forEach(s -> view.showMessage("  * " + s.getCode() + " - " + s.getTitle()
                            + " (" + s.getUnits() + " units @ " + String.format("%.2f", s.getFeePerUnit()) + "/unit)"));
                }
                case "7" -> handleShifting();
                case "0" -> { view.showMessage("Logging out from Registrar..."); return; }
                default -> view.showMessage("Invalid choice.");
            }
        }
    }

    private void enrollStudentFlow(String type, boolean isTransferee) {
        view.showMessage("\n[Registrar] Enroll " + type + " Student & Select Subjects");
        String snInput = input.readLine((isTransferee ? "Transferee " : "") + "Student No. (or press Enter to auto-generate): ");
        String name = input.readNonEmpty("Full Name: ");
        if (isTransferee) name = name + " [From: " + input.readNonEmpty("Previous School: ") + "]";
        String course = input.readNonEmpty((isTransferee ? "Target " : "") + "Course (e.g. BSIT): ");
        int yr = input.readInt((isTransferee ? "Credited " : "") + "Year Level (1-5): ", 1, 5);

        String id = service.addStudentWithType(snInput, name, course, yr, type);
        if (id == null) { view.showMessage("Student number already exists."); return; }

        view.showMessage(type + " student enrolled! Student ID: " + id);
        if (input.readLine("Enroll subjects now? (y/n): ").equalsIgnoreCase("y")) {
            String sem = input.readNonEmpty("Academic Period / Semester (e.g. 2026-2027 1st): ");
            do {
                view.showMessage("--- Available Subjects ---");
                service.getSubjects().forEach(s -> view.showMessage(s.getCode() + " - " + s.getTitle() + " (" + s.getUnits() + " units)"));
                view.showMessage(service.enrollStudent(id, input.readNonEmpty("Enter Subject Code to enroll: "), sem));
            } while (input.readLine("Enroll another subject? (y/n): ").equalsIgnoreCase("y"));
        }
        service.syncStudentToAdminAndPortal(id);
        if (input.readLine("Proceed to Cashiering to set fees / waive / pay tuition now? (y/n): ").equalsIgnoreCase("y")) {
            handleCashier(id);
        }
    }

    private void handleShifting() {
        view.showMessage("\n[Registrar Step 7] Shifting Requests\n1. Fill Up / Submit Shifting Request\n2. View & Approve / Reject Shifting Requests");
        if (input.readLine("Select action (1 or 2, or Enter to view/process directly): ").equals("1")) {
            String res = service.submitShiftingRequest(input.readNonEmpty("Student No.: "),
                    input.readNonEmpty("Target Course (e.g. BSCS): "), input.readNonEmpty("Reason for shifting: "));
            view.showMessage("Shifting request status: OKAY / NADAWAT NA. " + res);
            return;
        }
        var reqs = service.getShiftingRequests();
        if (reqs.isEmpty()) { view.showMessage("No shifting requests found."); return; }
        reqs.forEach(r -> view.showMessage(r + " | OKAY / NADAWAT NA"));
        String reqId = input.readLine("Enter Request ID to process (or Enter to skip): ");
        if (!reqId.isEmpty()) {
            boolean approve = input.readLine("Approve request? (y/n): ").equalsIgnoreCase("y");
            view.showMessage(service.processShiftingRequest(reqId, approve) ? "Shifting request updated and synced." : "Request ID not found.");
        }
    }

    private void handleCashier(String preselectedSn) {
        while (true) {
            view.showCashierMenu();
            String choice = input.readLine("Choice: ");
            if (choice.equals("0")) { view.showMessage("Logging out from Cashiering..."); return; }
            if (!choice.matches("[1-5]")) { view.showMessage("Invalid choice."); continue; }

            String sn = askStudentNo(preselectedSn);
            Student st = service.findStudent(sn);
            if (st == null) { view.showMessage("Student not found."); continue; }

            switch (choice) {
                case "1" -> {
                    double lab = input.readPositiveDouble("Laboratory Fee Total: ");
                    double misc = input.readPositiveDouble("General / Miscellaneous Fee Total: ");
                    service.setAdditionalFees(sn, lab, misc);
                    if (input.readLine("Apply Waive / Discount amount now? (y/n): ").equalsIgnoreCase("y")) {
                        service.waiveFee(sn, input.readPositiveDouble("Waive / Discount Amount: "));
                    }
                    service.syncStudentToAdminAndPortal(sn);
                    view.showMessage("Fees updated and synced to Admin & Student Portal.");
                    showBilling(service.findStudent(sn), sn);
                }
                case "2" -> {
                    service.waiveFee(sn, input.readPositiveDouble("Waive / Discount Amount: "));
                    service.syncStudentToAdminAndPortal(sn);
                    view.showMessage("Waive/discount applied and synced to Admin & Student Portal.");
                    showBilling(service.findStudent(sn), sn);
                }
                case "3" -> {
                    showBilling(st, sn);
                    double amt = input.readPositiveDouble("Payment Amount: ");
                    String method = input.readNonEmpty("Payment Method (CASH / GCASH / BANK / CARD): ");
                    view.showMessage(service.recordPayment(sn, amt, method));
                    showBilling(st, sn);
                }
                case "4" -> showBilling(st, sn);
                case "5" -> view.printPayments(service.getPaymentsByStudent(sn));
            }
        }
    }

    private String askStudentNo(String preselected) {
        if (preselected != null && !preselected.isBlank()) {
            String typed = input.readLine("Student No. [Enter for " + preselected + "]: ");
            return typed.isEmpty() ? preselected : typed;
        }
        return input.readNonEmpty("Student No.: ");
    }

    private void handleStudent(User user) {
        Student base = service.resolveStudentForPortal(user.getLinkedId());
        String fallbackId = base != null ? base.getStudentNumber() : user.getLinkedId();
        while (true) {
            view.showStudentMenu(fallbackId);
            String c = input.readLine("Choice: ");
            if (c.equals("0")) { view.showMessage("Logging out from Student Portal..."); return; }
            Student cur = service.resolveStudentForPortal(user.getLinkedId());
            String id = cur != null ? cur.getStudentNumber() : fallbackId;
            switch (c) {
                case "1" -> view.showMessage(cur == null ? "No student record found yet." : cur.toString());
                case "2" -> {
                    var lines = service.getStudentEnrolledSubjectsWithInstructor(id);
                    if (lines.isEmpty()) view.showMessage("Status: [NOT ENROLLED] - No enrolled subjects yet for Student ID " + id + ".");
                    else lines.forEach(l -> view.showMessage("  " + l));
                }
                case "3" -> {
                    if (cur == null) view.showMessage("No student record found yet.");
                    else showBilling(cur, cur.getStudentNumber());
                }
                case "4" -> {
                    String res = service.submitShiftingRequest(id, input.readNonEmpty("Target Course (e.g. BSCS): "), input.readNonEmpty("Reason for shifting: "));
                    view.showMessage("Shifting request status: OKAY / NADAWAT NA. " + res);
                }
                case "5" -> {
                    var reqs = service.getShiftingRequestsByStudent(id);
                    if (reqs.isEmpty()) view.showMessage("No shifting requests found.");
                    else reqs.forEach(r -> view.showMessage(r + " | OKAY / NADAWAT NA"));
                }
                default -> view.showMessage("Invalid choice.");
            }
        }
    }

    private void handleInstructor(User user) {
        String instId = user.getLinkedId();
        while (true) {
            view.showInstructorMenu(instId);
            switch (input.readLine("Choice: ")) {
                case "1" -> view.printSubjects(service.getSubjectsForInstructor(instId));
                case "2" -> {
                    var roster = service.getEnrollmentsBySubject(input.readNonEmpty("Subject Code: "));
                    view.printEnrollments(roster);
                    if (!roster.isEmpty()) {
                        String recId = input.readLine("Enter Enrollment Record ID to grade (or Enter to skip): ");
                        if (!recId.isEmpty()) {
                            view.showMessage(service.updateGrade(recId, input.readNonEmpty("Grade (e.g. 1.00, 1.25, INC): "))
                                    ? "Grade encoded." : "Record ID not found.");
                        }
                    }
                }
                case "0" -> { view.showMessage("Logging out from Instructor Portal..."); return; }
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
        view.showMessage(service.addSubject(code, title, units, fee, inst) ? "Subject added." : "Subject code already exists.");
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