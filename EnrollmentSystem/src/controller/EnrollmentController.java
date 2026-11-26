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
        this.svc = svc;
        this.view = view;
        this.in = in;
    }

    public void run() {
        view.showBanner();
        while (true) {
            view.showMainMenu();
            String ch = in.readLine("Choice: ");
            if (ch.equals("2")) {
                view.showMessage("Goodbye!");
                break;
            }
            if (!ch.equals("1")) {
                view.showMessage("Invalid option.");
                continue;
            }
            User u = svc.login(in.readNonEmpty("Username: "), in.readPassword("Password: "));
            if (u == null) {
                view.showMessage("Invalid login.");
                continue;
            }
            view.showMessage("\nWelcome, " + u.getUsername() + " [" + u.getRole() + "]");
            String r = u.getRole().trim().toUpperCase();
            if (r.contains("ADMIN")) admin();
            else if (r.contains("REGISTRAR")) registrar();
            else if (r.contains("CASHIER")) cashier(null);
            else if (r.contains("INSTRUCTOR")) instructor(u);
            else view.showMessage("No Student Portal. Please proceed to Registrar for Enrollment.");
        }
    }

    private boolean isYes(String s) {
        return s != null && (s.trim().equalsIgnoreCase("y") || s.trim().equalsIgnoreCase("yes"));
    }

    private String readCourseChoice() {
        view.showMessage("Courses: 1. BSIT | 2. BSCS | 3. BSIS | 4. ACT | 5. ENTREP (or type course code)");
        String pick = in.readNonEmpty("Course: ").trim().toUpperCase();
        return switch (pick) {
            case "1" -> "BSIT";
            case "2" -> "BSCS";
            case "3" -> "BSIS";
            case "4" -> "ACT";
            case "5" -> "ENTREP";
            default -> pick;
        };
    }

    private int readYearFlexible() {
        while (true) {
            String raw = in.readNonEmpty("Year (e.g. 2 or 2ND YR): ").trim().toUpperCase();
            String digits = raw.replaceAll("\\D+", "");
            if (!digits.isEmpty()) {
                int y = Integer.parseInt(digits);
                if (y >= 1 && y <= 5) return y;
            }
            if (raw.contains("FIRST") || raw.contains("1ST")) return 1;
            if (raw.contains("SECOND") || raw.contains("2ND")) return 2;
            if (raw.contains("THIRD") || raw.contains("3RD")) return 3;
            if (raw.contains("FOURTH") || raw.contains("4TH")) return 4;
            if (raw.contains("FIFTH") || raw.contains("5TH")) return 5;
            view.showMessage("Invalid year (1-5). Please try again.");
        }
    }

    private void showEnrolledStudentsWithDetails() {
        var list = svc.getStudents();
        if (list.isEmpty()) {
            view.showMessage("No enrolled students.");
            return;
        }
        for (Student s : list) {
            var recs = svc.getEnrollmentsByStudent(s.getStudentNumber());
            String subs = recs.isEmpty()
                    ? "None"
                    : String.join(", ", recs.stream().map(EnrollmentRecord::getSubjectCode).toList());
            view.showMessage("  * " + s.getStudentNumber() + " - " + s.getFullName()
                    + " | Course: " + s.getCourse()
                    + " | Yr: " + s.getYearLevel()
                    + " | Subjects: " + subs
                    + " | Status: " + s.getStatus());
        }
    }

    private void viewShiftedRecords() {
        var reqs = svc.getShiftingRequests();
        if (reqs.isEmpty()) {
            view.showMessage("No shifted records.");
            return;
        }
        view.showMessage("\n--- SHIFTED RECORDS ---");
        reqs.forEach(r -> view.showMessage("  * " + r + " | Successfully shifted"));
    }

    private void admin() {
        while (true) {
            view.showAdminMenu();
            switch (in.readLine("Choice: ")) {
                case "1" -> {
                    String role = in.readNonEmpty("Role (CASHIER / REGISTRAR): ");
                    if (role.trim().equalsIgnoreCase("STUDENT")) {
                        view.showMessage("STUDENT not allowed.");
                        break;
                    }
                    view.showMessage(svc.addUser(
                            in.readNonEmpty("Username: "),
                            in.readPassword("Password: "),
                            role,
                            in.readNonEmpty("Linked ID: ")) ? "Added." : "Failed.");
                }
                case "2" -> view.printUsers(svc.getAddedUsersOnly());
                case "3" -> svc.getSubjects().forEach(s -> view.showMessage("  * " + s.getCode() + " - " + s.getTitle() + " (" + s.getUnits() + "u) | " + s.getInstructor()));
                case "4" -> {
                    svc.getAdminEnrollmentReport().forEach(l -> view.showMessage("  * " + l));
                    svc.getAdminSyncedRecords().forEach(r -> view.showMessage("  - " + r));
                }
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
            view.showMessage("6. View Shifted Records");
            view.showMessage("0. Logout");
            view.showMessage("=====================================================");
            switch (in.readLine("Choice: ")) {
                case "1" -> enrollFlow("REGULAR", false);
                case "2" -> enrollFlow("TRANSFEREE", true);
                case "3" -> showEnrolledStudentsWithDetails();
                case "6" -> viewShiftedRecords();
                case "0" -> { return; }
                default -> view.showMessage("Invalid.");
            }
        }
    }

    private void enrollFlow(String type, boolean trans) {
        view.showMessage("\n--- ENROLLMENT FORM (" + type + ") ---");
        String sn = in.readLine("Student No. (Enter=auto): ");
        String name = in.readNonEmpty("Full Name: ");
        if (trans) {
            String prevSchool = in.readNonEmpty("Prev School: ");
            name = name + " [From: " + prevSchool + "]";
        }
        String course = readCourseChoice();
        int yr = readYearFlexible();

        String id = svc.addStudentWithType(sn, name, course, yr, type);
        if (id == null) {
            view.showMessage("Exists.");
            return;
        }
        view.showMessage("Enrolled ID: " + id);

        if (isYes(in.readLine("Enroll subjects? (y/n): "))) {
            String sem = in.readNonEmpty("Semester: ");
            view.showMessage("--- SUBJECTS ---");
            svc.getSubjects().forEach(s -> view.showMessage("  * " + s.getCode() + " - " + s.getTitle()));
            do {
                view.showMessage(svc.enrollStudent(id, in.readNonEmpty("Subject Code: "), sem));
            } while (isYes(in.readLine("Another? (y/n): ")));
        }

        if (isYes(in.readLine("Shifting Request? (y/n): "))) {
            view.showMessage(svc.submitShiftingRequest(id, readCourseChoice(), in.readNonEmpty("Reason: ")) + " | Successfully shifted");
        }

        svc.syncStudent(id);
        if (isYes(in.readLine("Go to Cashier? (y/n): "))) cashier(id);
    }

    private void cashier(String pre) {
        while (true) {
            view.showMessage("\n=========== CASHIERING & TUITION MODULE ===========");
            view.showMessage("1. View Fees");
            view.showMessage("2. Set / Update Fees (Tuition, Reg, Lab, Misc, Waiver)");
            view.showMessage("3. Record Tuition Payment");
            view.showMessage("4. View Assessment Slip & Tuition Balance");
            view.showMessage("5. View Student Payment History");
            view.showMessage("6. View Students Record");
            view.showMessage("0. Logout");
            view.showMessage("==================================================");
            String ch = in.readLine("Choice: ");
            if (ch.equals("0")) return;
            if (ch.equals("6")) {
                showEnrolledStudentsWithDetails();
                continue;
            }
            if (ch.equals("1")) {
                String sn = (pre != null && !pre.isBlank() && in.readLine("Student No. [Enter=" + pre + "]: ").isEmpty())
                        ? pre
                        : in.readNonEmpty("Student No.: ");
                view.showMessage("\n--- FEES (" + sn + ") ---");
                view.showMessage("  * Tuition Fee: " + String.format("%.2f", svc.getTuitionFee(sn)));
                view.showMessage("  * Registration Fee: " + String.format("%.2f", svc.getRegistrationFee(sn)));
                view.showMessage("  * Lab Fee: " + String.format("%.2f", svc.getLabFee(sn)));
                view.showMessage("  * Misc Fee: " + String.format("%.2f", svc.getMiscFee(sn)));
                view.showMessage("  * Waive / Discount: " + String.format("%.2f", svc.getWaivedAmount(sn)));
                continue;
            }
            if (!ch.matches("[2-5]")) {
                view.showMessage("Invalid.");
                continue;
            }
            String sn = (pre != null && !pre.isBlank() && in.readLine("Student No. [Enter=" + pre + "]: ").isEmpty())
                    ? pre
                    : in.readNonEmpty("Student No.: ");
            Student st = svc.findStudent(sn);
            if (st == null) {
                view.showMessage("Not found.");
                continue;
            }
            switch (ch) {
                case "2" -> {
                    svc.setFees(sn,
                            in.readPositiveDouble("Tuition Fee: "),
                            in.readPositiveDouble("Registration Fee: "),
                            in.readPositiveDouble("Lab Fee: "),
                            in.readPositiveDouble("Misc Fee: "));
                    if (isYes(in.readLine("Discount / Waive? (y/n): "))) {
                        svc.waiveFee(sn, in.readPositiveDouble("Discount / Waive Amount: "));
                    }
                    billing(svc.findStudent(sn), sn);
                }
                case "3" -> {
                    billing(st, sn);
                    String res = svc.recordPayment(sn, in.readPositiveDouble("Amount: "), in.readNonEmpty("Method: "));
                    view.showMessage(res);
                    view.showMessage("SUCCESSFULLY ENROLLED!");
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
                    if (!r.isEmpty()) {
                        String rec = in.readLine("Record ID (Enter=skip): ");
                        if (!rec.isEmpty()) {
                            view.showMessage(svc.updateGrade(rec, in.readNonEmpty("Grade: ")) ? "Graded." : "Not found.");
                        }
                    }
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