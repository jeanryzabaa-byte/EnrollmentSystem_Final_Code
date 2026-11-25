package controller;

import model.*;
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
            String ch = input.readLine("Choice: ");
            if (ch.equals("2")) { view.showMessage("Goodbye! Thank you for using the system."); break; }
            if (!ch.equals("1")) { view.showMessage("Invalid option. Please choose 1 or 2."); continue; }

            User user = service.login(input.readNonEmpty("Username: "), input.readPassword("Password: "));
            if (user == null) { view.showMessage("Invalid username or password."); continue; }

            view.showMessage("\n====================================================\nWelcome, "
                    + user.getUsername() + " [" + user.getRole() + "]\n====================================================");
            String r = user.getRole() == null ? "" : user.getRole().trim().toUpperCase();
            if (r.contains("ADMIN")) handleAdmin();
            else if (r.contains("REGISTRAR")) handleRegistrar();
            else if (r.contains("CASHIER")) handleCashier(null);
            else if (r.contains("STUDENT")) handleStudent(user);
            else if (r.contains("INSTRUCTOR")) handleInstructor(user);
            else view.showMessage("Unknown role: " + user.getRole());
        }
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
                    view.showMessage(service.addUser(input.readNonEmpty("Set Username: "),
                            input.readPassword("Set Password: "), role, input.readNonEmpty("Linked ID: "))
                            ? "User account added successfully." : "Failed: duplicate username or invalid role.");
                }
                case "2" -> { view.showMessage("\n[Admin Step 2] Added User Accounts"); view.printUsers(service.getAddedUsersOnly()); }
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
                case "3" -> { view.showMessage("\n[Registrar Step 3] Registered Students"); view.printStudents(service.getStudents()); }
                case "4" -> {
                    view.showMessage("\n[Registrar Step 4] Student Enrollments");
                    service.getRegistrarEnrollmentSummary(input.readLine("Student No. (press Enter to display ALL): "))
                            .forEach(l -> view.showMessage("  " + l));
                }
                case "5" -> {
                    view.showMessage("\n[Subject Setup] Add Subject");
                    view.showMessage(service.addSubject(input.readNonEmpty("Subject Code (e.g. IT 205): "),
                            input.readNonEmpty("Subject Title: "), input.readInt("Units (1-10): ", 1, 10),
                            input.readPositiveDouble("Tuition Rate per Unit (e.g. 150.00): "),
                            input.readLine("Instructor Username (optional): "))
                            ? "Subject added." : "Subject code already exists.");
                }
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
        String name = input.readNonEmpty("Full Name: ")
                + (isTransferee ? " [From: " + input.readNonEmpty("Previous School: ") + "]" : "");
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
            view.showMessage("Shifting request status: OKAY / NADAWAT NA. " + service.submitShiftingRequest(
                    input.readNonEmpty("Student No.: "), input.readNonEmpty("Target Course (e.g. BSCS): "),
                    input.readNonEmpty("Reason for shifting: ")));
            return;
        }
        var reqs = service.getShiftingRequests();
        if (reqs.isEmpty()) { view.showMessage("No shifting requests found."); return; }
        reqs.forEach(r -> view.showMessage(r + " | OKAY / NADAWAT NA"));
        String reqId = input.readLine("Enter Request ID to process (or Enter to skip): ");
        if (!reqId.isEmpty()) {
            view.showMessage(service.processShiftingRequest(reqId, input.readLine("Approve request? (y/n): ").equalsIgnoreCase("y"))
                    ? "Shifting request updated and synced." : "Request ID not found.");
        }
    }

    private void handleCashier(String preselectedSn) {
        while (true) {
            view.showCashierMenu();
            String ch = input.readLine("Choice: ");
            if (ch.equals("0")) { view.showMessage("Logging out from Cashiering..."); return; }
            if (ch.equals("6")) {
                view.showMessage("\n[Cashier Step 6] Students Record");
                var students = service.getStudents();
                if (students.isEmpty()) view.showMessage("No student records found.");
                else students.forEach(s -> view.showMessage("  * " + s + " | Balance: "
                        + String.format("%.2f", service.calculateBalance(s.getStudentNumber()))));
                continue;
            }
            if (!ch.matches("[1-5]")) { view.showMessage("Invalid choice."); continue; }

            String sn = (preselectedSn != null && !preselectedSn.isBlank())
                    ? (input.readLine("Student No. [Enter for " + preselectedSn + "]: ").isEmpty() ? preselectedSn
                    : input.readLine("Student No. [Enter for " + preselectedSn + "]: "))
                    : input.readNonEmpty("Student No.: ");
            Student st = service.findStudent(sn);
            if (st == null) { view.showMessage("Student not found."); continue; }

            switch (ch) {
                case "1" -> {
                    service.setAdditionalFees(sn, input.readPositiveDouble("Laboratory Fee Total: "),
                            input.readPositiveDouble("General / Miscellaneous Fee Total: "));
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
                    view.showMessage(service.recordPayment(sn, amt, input.readNonEmpty("Payment Method (CASH / GCASH / BANK / CARD): ")));
                    showBilling(st, sn);
                }
                case "4" -> showBilling(st, sn);
                case "5" -> view.printPayments(service.getPaymentsByStudent(sn));
            }
        }
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
                case "3" -> { if (cur == null) view.showMessage("No student record found yet."); else showBilling(cur, cur.getStudentNumber()); }
                case "4" -> view.showMessage("Shifting request status: OKAY / NADAWAT NA. " + service.submitShiftingRequest(
                        id, input.readNonEmpty("Target Course (e.g. BSCS): "), input.readNonEmpty("Reason for shifting: ")));
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

    private void showBilling(Student st, String sn) {
        double tuition = service.calculateTotalAssessment(sn);
        double lab = service.getLabFee(sn);
        double misc = service.getMiscFee(sn);
        double waived = service.getWaivedAmount(sn);
        double total = Math.max(0.0, tuition + lab + misc - waived);
        double paid = service.calculateTotalPaid(sn);
        view.printDetailedBilling(st, tuition, lab, misc, waived, total, paid, Math.max(0.0, total - paid));
    }
}