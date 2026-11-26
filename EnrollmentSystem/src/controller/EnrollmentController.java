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
            else if (r.contains("INSTRUCTOR")) instructor(u);
            else view.showMessage("No Student Portal. Please proceed to Registrar for Enrollment.");
        }
    }

    private boolean isYes(String s) { return s != null && (s.trim().equalsIgnoreCase("y") || s.trim().equalsIgnoreCase("yes")); }

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
            view.showMessage("\n=========== REGISTRAR & ENROLLMENT MODULE ===========");
            view.showMessage("1. Enrollment Form (Regular Student)");
            view.showMessage("2. Enrollment Form (Transferee Student)");
            view.showMessage("3. View Enrolled Students");
            view.showMessage("4. Add Subject / Request Add Subject");
            view.showMessage("5. View All Subjects");
            view.showMessage("6. Shifting Requests (Fill Up / View & Process)");
            view.showMessage("0. Logout");
            view.showMessage("=====================================================");
            switch (in.readLine("Choice: ")) {
                case "1" -> enrollFlow("REGULAR", false);
                case "2" -> enrollFlow("TRANSFEREE", true);
                case "3" -> view.printStudents(svc.getStudents());
                case "4" -> {
                    String mode = in.readLine("1. Add New Subject | 2. Request/Enroll Subject to Student: ");
                    if (mode.equals("1")) {
                        view.showMessage(svc.addSubject(in.readNonEmpty("Code: "), in.readNonEmpty("Title: "), in.readInt("Units (1-10): ", 1, 10), in.readPositiveDouble("Rate/Unit: "), in.readLine("Instructor (opt): ")) ? "Subject Added." : "Exists.");
                    } else {
                        String sn = in.readNonEmpty("Student No.: ");
                        svc.getSubjects().forEach(s -> view.showMessage("  * " + s.getCode() + " - " + s.getTitle() + " (" + s.getUnits() + "u)"));
                        view.showMessage(svc.enrollStudent(sn, in.readNonEmpty("Subject Code: "), in.readNonEmpty("Semester: ")));
                    }
                }
                case "5" -> svc.getSubjects().forEach(s -> view.showMessage("  * " + s.getCode() + " - " + s.getTitle() + " (" + s.getUnits() + "u @ " + String.format("%.2f", s.getFeePerUnit()) + ") | " + s.getInstructor()));
                case "6" -> shifting();
                case "0" -> { return; }
                default -> view.showMessage("Invalid.");
            }
        }
    }

    private void enrollFlow(String type, boolean trans) {
        view.showMessage("\n--- ENROLLMENT FORM (" + type + ") ---");
        String id = svc.addStudentWithType(in.readLine("Student No. (Enter=auto): "),
                in.readNonEmpty("Full Name: ") + (trans ? " [From: " + in.readNonEmpty("Prev School: ") + "]" : ""),
                in.readNonEmpty("Course: "), in.readInt("Year (1-5): ", 1, 5), type);
        if (id == null) { view.showMessage("Exists."); return; }
        view.showMessage("Enrolled ID: " + id);

        if (isYes(in.readLine("View & Select Subjects now? (y/n): "))) {
            String sem = in.readNonEmpty("Semester: ");
            do {
                view.showMessage("\n--- AVAILABLE SUBJECTS ---");
                svc.getSubjects().forEach(s -> view.showMessage("  * " + s.getCode() + " - " + s.getTitle() + " (" + s.getUnits() + "u) | " + s.getInstructor()));
                view.showMessage(svc.enrollStudent(id, in.readNonEmpty("Choose Subject Code: "), sem));
            } while (isYes(in.readLine("Add another subject? (y/n): ")));
        }

        if (isYes(in.readLine("Submit Shifting Request? (y/n): "))) {
            view.showMessage(svc.submitShiftingRequest(id, in.readNonEmpty("Target Course: "), in.readNonEmpty("Reason: ")) + " | Successfully shifted");
        }

        svc.syncStudent(id);
        if (isYes(in.readLine("Go to Cashier? (y/n): "))) cashier(id);
    }

    private void shifting() {
        if (in.readLine("1. Fill Up Shifting Request | 2. View/Process: ").equals("1")) {
            view.showMessage(svc.submitShiftingRequest(in.readNonEmpty("Student No.: "), in.readNonEmpty("Target Course: "), in.readNonEmpty("Reason: ")) + " | Successfully shifted");
            return;
        }
        var reqs = svc.getShiftingRequests();
        if (reqs.isEmpty()) { view.showMessage("None."); return; }
        reqs.forEach(r -> view.showMessage(r + " | Successfully shifted"));
        String id = in.readLine("Request ID (Enter=skip): ");
        if (!id.isEmpty()) view.showMessage(svc.processShiftingRequest(id, isYes(in.readLine("Approve? (y/n): "))) ? "Updated." : "Not found.");
    }

    private void cashier(String pre) {
        while (true) {
            view.showCashierMenu();
            String ch = in.readLine("Choice: ");
            if (ch.equals("0")) return;
            if (ch.equals("6")) { svc.getStudents().forEach(s -> view.showMessage("  * " + s + " | Bal: " + String.format("%.2f", svc.calculateBalance(s.getStudentNumber())))); continue; }
            if (ch.equals("7")) {
                String sn = (pre != null && !pre.isBlank()) ? pre : in.readNonEmpty("Student No.: ");
                view.showMessage("\n--- FEES (" + sn + ") ---");
                view.showMessage("  * Tuition Fee: " + String.format("%.2f", svc.getTuitionFee(sn)));
                view.showMessage("  * Registration Fee: " + String.format("%.2f", svc.getRegistrationFee(sn)));
                view.showMessage("  * Lab: " + String.format("%.2f", svc.getLabFee(sn)));
                view.showMessage("  * Misc: " + String.format("%.2f", svc.getMiscFee(sn)));
                view.showMessage("  * Waive: " + String.format("%.2f", svc.getWaivedAmount(sn)));
                continue;
            }
            if (!ch.matches("[1-5]")) { view.showMessage("Invalid."); continue; }
            String sn = (pre != null && !pre.isBlank() && in.readLine("Student No. [Enter=" + pre + "]: ").isEmpty()) ? pre : in.readNonEmpty("Student No.: ");
            Student st = svc.findStudent(sn);
            if (st == null) { view.showMessage("Not found."); continue; }
            switch (ch) {
                case "1" -> {
                    svc.setFees(sn,
                            in.readPositiveDouble("Tuition Fee: "),
                            in.readPositiveDouble("Registration Fee: "),
                            in.readPositiveDouble("Lab Fee: "),
                            in.readPositiveDouble("Misc Fee: "));
                    if (isYes(in.readLine("Waive? (y/n): "))) svc.waiveFee(sn, in.readPositiveDouble("Waive Amount: "));
                    billing(svc.findStudent(sn), sn);
                }
                case "2" -> { svc.waiveFee(sn, in.readPositiveDouble("Waive Amount: ")); billing(svc.findStudent(sn), sn); }
                case "3" -> {
                    billing(st, sn);
                    String res = svc.recordPayment(sn, in.readPositiveDouble("Amount: "), in.readNonEmpty("Method: "));
                    view.showMessage(res);
                    if (res.startsWith("Paid.")) view.showMessage("SUCCESSFULLY ENROLLED!");
                    billing(svc.findStudent(sn), sn);
                }
                case "4" -> billing(st, sn);
                case "5" -> view.printPayments(svc.getPaymentsByStudent(sn));
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
        double tu = svc.getTuitionFee(sn), reg = svc.getRegistrationFee(sn);
        double t = tu + reg, l = svc.getLabFee(sn), m = svc.getMiscFee(sn), w = svc.getWaivedAmount(sn);
        double tot = Math.max(0, t + l + m - w), p = svc.calculateTotalPaid(sn);
        view.showMessage("  [Breakdown] Tuition: " + String.format("%.2f", tu) + " | Registration Fee: " + String.format("%.2f", reg));
        view.printDetailedBilling(st, t, l, m, w, tot, p, Math.max(0, tot - p));
    }
}