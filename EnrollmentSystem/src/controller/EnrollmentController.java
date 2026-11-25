package controller;

import model.*;
import service.EnrollmentService;
import util.InputValidator;
import view.ConsoleView;

public class EnrollmentController {
    private final EnrollmentService svc;
    private final ConsoleView view;
    private final InputValidator in;

    public EnrollmentController(EnrollmentService svc, ConsoleView view, InputValidator in) {
        this.svc = svc; this.view = view; this.in = in;
    }

    public void run() {
        view.showBanner();
        while (true) {
            view.showMainMenu();
            String ch = in.readLine("Choice: ");
            if (ch.equals("2")) { view.showMessage("Goodbye!"); break; }
            if (!ch.equals("1")) { view.showMessage("Invalid option."); continue; }
            User u = svc.login(in.readNonEmpty("Username: "), in.readPassword("Password: "));
            if (u == null) { view.showMessage("Invalid login."); continue; }
            view.showMessage("\nWelcome, " + u.getUsername() + " [" + u.getRole() + "]");
            String r = u.getRole().trim().toUpperCase();
            if (r.contains("ADMIN")) admin();
            else if (r.contains("REGISTRAR")) registrar();
            else if (r.contains("CASHIER")) cashier(null);
            else if (r.contains("STUDENT")) student(u);
            else if (r.contains("INSTRUCTOR")) instructor(u);
        }
    }

    private void admin() {
        while (true) {
            view.showAdminMenu();
            switch (in.readLine("Choice: ")) {
                case "1" -> {
                    String role = in.readNonEmpty("Role (CASHIER / REGISTRAR): ");
                    if (role.trim().equalsIgnoreCase("STUDENT")) { view.showMessage("STUDENT not allowed."); break; }
                    view.showMessage(svc.addUser(in.readNonEmpty("Username: "), in.readPassword("Password: "), role, in.readNonEmpty("Linked ID: ")) ? "Added." : "Failed.");
                }
                case "2" -> view.printUsers(svc.getAddedUsersOnly());
                case "3" -> svc.getSubjects().forEach(s -> view.showMessage("  * " + s.getCode() + " - " + s.getTitle() + " (" + s.getUnits() + "u) | " + s.getInstructor()));
                case "4" -> { svc.getAdminEnrollmentReport().forEach(l -> view.showMessage("  * " + l)); svc.getAdminSyncedRecords().forEach(r -> view.showMessage("  - " + r)); }
                case "0" -> { return; }
                default -> view.showMessage("Invalid.");
            }
        }
    }

    private void registrar() {
        while (true) {
            view.showRegistrarMenu();
            switch (in.readLine("Choice: ")) {
                case "1" -> enrollFlow("REGULAR", false);
                case "2" -> enrollFlow("TRANSFEREE", true);
                case "3" -> view.printStudents(svc.getStudents());
                case "4" -> view.showMessage(svc.addSubject(in.readNonEmpty("Code: "), in.readNonEmpty("Title: "), in.readInt("Units (1-10): ", 1, 10), in.readPositiveDouble("Rate/Unit: "), in.readLine("Instructor (opt): ")) ? "Added." : "Exists.");
                case "5" -> svc.getSubjects().forEach(s -> view.showMessage("  * " + s.getCode() + " - " + s.getTitle() + " (" + s.getUnits() + "u @ " + String.format("%.2f", s.getFeePerUnit()) + ")"));
                case "6" -> shifting();
                case "0" -> { return; }
                default -> view.showMessage("Invalid.");
            }
        }
    }

    private void enrollFlow(String type, boolean trans) {
        String id = svc.addStudentWithType(in.readLine("Student No. (Enter=auto): "),
                in.readNonEmpty("Name: ") + (trans ? " [From: " + in.readNonEmpty("Prev School: ") + "]" : ""),
                in.readNonEmpty("Course: "), in.readInt("Year (1-5): ", 1, 5), type);
        if (id == null) { view.showMessage("Exists."); return; }
        view.showMessage("Enrolled ID: " + id);
        if (in.readLine("Enroll subjects? (y/n): ").equalsIgnoreCase("y")) {
            String sem = in.readNonEmpty("Semester: ");
            do {
                svc.getSubjects().forEach(s -> view.showMessage("  " + s.getCode() + " - " + s.getTitle() + " (" + s.getUnits() + "u)"));
                view.showMessage(svc.enrollStudent(id, in.readNonEmpty("Subject Code: "), sem));
            } while (in.readLine("Another? (y/n): ").equalsIgnoreCase("y"));
        }
        svc.syncStudent(id);
        if (in.readLine("Go to Cashier? (y/n): ").equalsIgnoreCase("y")) cashier(id);
    }

    private void shifting() {
        if (in.readLine("1. Submit | 2. View/Process: ").equals("1")) {
            view.showMessage(svc.submitShiftingRequest(in.readNonEmpty("Student No.: "), in.readNonEmpty("Target Course: "), in.readNonEmpty("Reason: ")) + " | OKAY / NADAWAT NA");
            return;
        }
        var reqs = svc.getShiftingRequests();
        if (reqs.isEmpty()) { view.showMessage("None."); return; }
        reqs.forEach(r -> view.showMessage(r + " | OKAY / NADAWAT NA"));
        String id = in.readLine("Request ID (Enter=skip): ");
        if (!id.isEmpty()) view.showMessage(svc.processShiftingRequest(id, in.readLine("Approve? (y/n): ").equalsIgnoreCase("y")) ? "Updated." : "Not found.");
    }

    private void cashier(String pre) {
        while (true) {
            view.showCashierMenu();
            String ch = in.readLine("Choice: ");
            if (ch.equals("0")) return;
            if (ch.equals("6")) { svc.getStudents().forEach(s -> view.showMessage("  * " + s + " | Bal: " + String.format("%.2f", svc.calculateBalance(s.getStudentNumber())))); continue; }
            if (ch.equals("7")) {
                view.showMessage("\n--- FEES ---");
                view.showMessage("  * Registration Fee: 150.00");
                svc.getSubjects().forEach(s -> view.showMessage("  * " + s.getCode() + ": 150.00"));
                continue;
            }
            if (!ch.matches("[1-5]")) { view.showMessage("Invalid."); continue; }
            String sn = (pre != null && !pre.isBlank() && in.readLine("Student No. [Enter=" + pre + "]: ").isEmpty()) ? pre : in.readNonEmpty("Student No.: ");
            Student st = svc.findStudent(sn);
            if (st == null) { view.showMessage("Not found."); continue; }
            switch (ch) {
                case "1" -> { svc.setAdditionalFees(sn, in.readPositiveDouble("Lab: "), in.readPositiveDouble("Misc: ")); if (in.readLine("Waive? (y/n): ").equalsIgnoreCase("y")) svc.waiveFee(sn, in.readPositiveDouble("Waive: ")); billing(svc.findStudent(sn), sn); }
                case "2" -> { svc.waiveFee(sn, in.readPositiveDouble("Waive: ")); billing(svc.findStudent(sn), sn); }
                case "3" -> { billing(st, sn); view.showMessage(svc.recordPayment(sn, in.readPositiveDouble("Amount: "), in.readNonEmpty("Method: "))); billing(st, sn); }
                case "4" -> billing(st, sn);
                case "5" -> view.printPayments(svc.getPaymentsByStudent(sn));
            }
        }
    }

    private void student(User u) {
        Student base = svc.resolveStudentForPortal(u.getLinkedId());
        String fb = base != null ? base.getStudentNumber() : u.getLinkedId();
        while (true) {
            view.showStudentMenu(fb);
            String c = in.readLine("Choice: ");
            if (c.equals("0")) return;
            Student cur = svc.resolveStudentForPortal(u.getLinkedId());
            String id = cur != null ? cur.getStudentNumber() : fb;
            switch (c) {
                case "1" -> view.showMessage(cur == null ? "No record." : cur.toString());
                case "2" -> { var lines = svc.getStudentSubjectsWithInstructor(id); if (lines.isEmpty()) view.showMessage("[NOT ENROLLED]"); else lines.forEach(l -> view.showMessage("  " + l)); }
                case "3" -> { if (cur == null) view.showMessage("No record."); else billing(cur, id); }
                case "4" -> view.showMessage(svc.submitShiftingRequest(id, in.readNonEmpty("Target: "), in.readNonEmpty("Reason: ")) + " | OKAY / NADAWAT NA");
                case "5" -> view.printShiftingRequests(svc.getShiftingRequestsByStudent(id));
                default -> view.showMessage("Invalid.");
            }
        }
    }

    private void instructor(User u) {
        String id = u.getLinkedId();
        while (true) {
            view.showInstructorMenu(id);
            switch (in.readLine("Choice: ")) {
                case "1" -> view.printSubjects(svc.getSubjectsForInstructor(id));
                case "2" -> {
                    var r = svc.getEnrollmentsBySubject(in.readNonEmpty("Subject Code: "));
                    view.printEnrollments(r);
                    if (!r.isEmpty()) { String rec = in.readLine("Record ID (Enter=skip): "); if (!rec.isEmpty()) view.showMessage(svc.updateGrade(rec, in.readNonEmpty("Grade: ")) ? "Graded." : "Not found."); }
                }
                case "0" -> { return; }
                default -> view.showMessage("Invalid.");
            }
        }
    }

    private void billing(Student st, String sn) {
        double t = svc.calculateTotalAssessment(sn), l = svc.getLabFee(sn), m = svc.getMiscFee(sn), w = svc.getWaivedAmount(sn);
        double tot = Math.max(0, t + l + m - w), p = svc.calculateTotalPaid(sn);
        view.printDetailedBilling(st, t, l, m, w, tot, p, Math.max(0, tot - p));
    }
}