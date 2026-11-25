package controller;

import model.Student;
import model.User;
import service.EnrollmentService;
import util.InputValidator;
import view.ConsoleView;

import java.util.List;

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

            String username = input.readNonEmpty("Username: ");
            String password = input.readPassword("Password: ");
            User loggedIn = service.login(username, password);

            if (loggedIn == null) {
                view.showMessage("Invalid username or password.");
                continue;
            }

            view.showMessage("\n====================================================");
            view.showMessage("Welcome, " + loggedIn.getUsername() + " [" + loggedIn.getRole() + "]");
            view.showMessage("====================================================");
            routeByRole(loggedIn);
        }
    }

    private void routeByRole(User user) {
        String role = user.getRole() == null ? "" : user.getRole().trim().toUpperCase();
        if (role.contains("ADMIN")) {
            handleAdmin();
        } else if (role.contains("REGISTRAR")) {
            handleRegistrar();
        } else if (role.contains("CASHIER")) {
            handleCashierForStudent(null);
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
                    view.showMessage("\n[Admin Step 1] Register / Provision User Account");
                    List<String> pendingIds = service.getPendingStudentIdsForAccount();
                    if (!pendingIds.isEmpty()) {
                        view.showMessage("Enrolled Student IDs from Registrar ready for account: " + pendingIds);
                    }
                    String r = input.readNonEmpty("Role (ADMIN / REGISTRAR / CASHIER / STUDENT / INSTRUCTOR): ");
                    String lid;
                    if (r.trim().equalsIgnoreCase("STUDENT") && !pendingIds.isEmpty()) {
                        String picked = input.readLine("Enter Student ID from list above (or Enter for custom ID): ");
                        lid = picked.isEmpty() ? input.readNonEmpty("Linked Student ID: ") : picked;
                    } else {
                        lid = input.readNonEmpty("Linked ID (Student No. or Staff ID): ");
                    }
                    String u = input.readNonEmpty("Set Username: ");
                    String p = input.readPassword("Set Password: ");
                    view.showMessage(service.addUser(u, p, r, lid)
                            ? "Account registered and linked to ID '" + lid + "'."
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
                    view.showMessage("\n[Admin Step 5] Master Student & Synced Portal Records");
                    view.printStudents(service.getStudents());
                    List<String> synced = service.getAdminSyncedRecords();
                    if (!synced.isEmpty()) {
                        view.showMessage("\n--- Synced Enrollment & Billing Records (Visible in Student Portal) ---");
                        for (String rec : synced) {
                            view.showMessage("  * " + rec);
                        }
                    }
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
                    view.showMessage("\n[Registrar Step 1] Enroll Regular Student & Select Subjects");
                    String snInput = input.readLine("Student No. (or press Enter to auto-generate): ");
                    String name = input.readNonEmpty("Full Name: ");
                    String course = input.readNonEmpty("Course (e.g. BSIT): ");
                    int yr = input.readInt("Year Level (1-5): ", 1, 5);
                    String assignedId = service.addStudentWithType(snInput, name, course, yr, "REGULAR");
                    if (assignedId == null) {
                        view.showMessage("Student number already exists.");
                    } else {
                        view.showMessage("Regular student enrolled! Student ID: " + assignedId);
                        enrollSubjectsInFlow(assignedId);
                        service.syncStudentToAdminAndPortal(assignedId);
                        promptProceedToCashier(assignedId);
                    }
                }
                case "2" -> {
                    view.showMessage("\n[Registrar Step 2] Enroll Transferee Student & Select Subjects");
                    String snInput = input.readLine("Transferee Student No. (or press Enter to auto-generate): ");
                    String name = input.readNonEmpty("Full Name: ");
                    String prevSchool = input.readNonEmpty("Previous School: ");
                    String course = input.readNonEmpty("Target Course (e.g. BSIT): ");
                    int yr = input.readInt("Credited Year Level (1-5): ", 1, 5);
                    String assignedId = service.addStudentWithType(snInput, name + " [From: " + prevSchool + "]", course, yr, "TRANSFEREE");
                    if (assignedId == null) {
                        view.showMessage("Student number already exists.");
                    } else {
                        view.showMessage("Transferee student enrolled! Student ID: " + assignedId);
                        enrollSubjectsInFlow(assignedId);
                        service.syncStudentToAdminAndPortal(assignedId);
                        promptProceedToCashier(assignedId);
                    }
                }
                case "3" -> {
                    view.showMessage("\n[Registrar Step 3] Registered Students");
                    view.printStudents(service.getStudents());
                }
                case "4" -> {
                    view.showMessage("\n[Registrar Step 4] Student Enrollments (ENROLLED / NOT ENROLLED)");
                    String sn = input.readLine("Student No. (press Enter to display ALL): ");
                    if (!sn.isBlank()) {
                        Student st = service.findStudent(sn);
                        if (st != null) {
                            service.syncStudentToAdminAndPortal(sn);
                        }
                    }
                    List<String> overview = service.getFormattedEnrollmentOverview(sn);
                    for (String line : overview) {
                        view.showMessage("  " + line);
                    }
                }
                case "5" -> handleAddSubject();
                case "6" -> handleRegistrarShiftingMenu();
                case "0" -> {
                    view.showMessage("Logging out from Registrar...");
                    return;
                }
                default -> view.showMessage("Invalid choice.");
            }
        }
    }

    private void enrollSubjectsInFlow(String studentNo) {
        String addSubj = input.readLine("Enroll subjects for student " + studentNo + " now? (y/n): ");
        if (!addSubj.equalsIgnoreCase("y")) return;

        String sem = input.readNonEmpty("Academic Period / Semester (e.g. 2026-2027 1st): ");
        while (true) {
            view.printSubjects(service.getSubjects());
            String sc = input.readNonEmpty("Enter Subject Code to enroll: ");
            view.showMessage(service.enrollStudent(studentNo, sc, sem));
            String more = input.readLine("Enroll another subject for " + studentNo + "? (y/n): ");
            if (!more.equalsIgnoreCase("y")) break;
        }
    }

    private void promptProceedToCashier(String studentNo) {
        String goCashier = input.readLine("Proceed to Cashiering to set fees / waive / pay tuition now? (y/n): ");
        if (goCashier.equalsIgnoreCase("y")) {
            handleCashierForStudent(studentNo);
        }
    }

    private void handleRegistrarShiftingMenu() {
        view.showMessage("\n[Registrar Step 6] Shifting Requests");
        view.showMessage("1. Fill Up / Submit Shifting Request");
        view.showMessage("2. View & Approve / Reject Shifting Requests");
        String sub = input.readLine("Select action (1 or 2, or Enter to view/process directly): ");
        if (sub.equals("1")) {
            String sn = input.readNonEmpty("Student No.: ");
            String target = input.readNonEmpty("Target Course (e.g. BSCS): ");
            String reason = input.readNonEmpty("Reason for shifting: ");
            String res = service.submitShiftingRequest(sn, target, reason);
            view.showMessage("Shifting request status: OKAY / NADAWAT NA. " + res);
            return;
        }
        var reqs = service.getShiftingRequests();
        if (reqs.isEmpty()) {
            view.showMessage("No shifting requests found.");
        } else {
            for (var req : reqs) {
                view.showMessage(req.toString() + " | OKAY / NADAWAT NA");
            }
            String reqId = input.readLine("Enter Request ID to process (or Enter to skip): ");
            if (!reqId.isEmpty()) {
                boolean approve = input.readLine("Approve request? (y/n): ").equalsIgnoreCase("y");
                view.showMessage(service.processShiftingRequest(reqId, approve)
                        ? "Shifting request updated and synced."
                        : "Request ID not found.");
            }
        }
    }

    private void handleCashierForStudent(String preselectedStudentNo) {
        while (true) {
            view.showCashierMenu();
            if (preselectedStudentNo != null) {
                view.showMessage("[Active Student from Registrar: " + preselectedStudentNo + "]");
            }
            String c = input.readLine("Choice: ");
            switch (c) {
                case "1" -> {
                    view.showMessage("\n[Cashier Step 1] Student Assessment Slip & Tuition Balance");
                    String sn = askStudentNo(preselectedStudentNo);
                    Student st = service.findStudent(sn);
                    if (st == null) view.showMessage("Student not found.");
                    else showBilling(st, sn);
                }
                case "2" -> {
                    view.showMessage("\n[Cashier Step 2] Set Lab Fee, Misc Fee & Optional Waive/Discount");
                    String sn = askStudentNo(preselectedStudentNo);
                    if (service.findStudent(sn) == null) {
                        view.showMessage("Student not found.");
                    } else {
                        double lab = input.readPositiveDouble("Laboratory Fee Total: ");
                        double misc = input.readPositiveDouble("General / Miscellaneous Fee Total: ");
                        service.setAdditionalFees(sn, lab, misc);
                        String wAns = input.readLine("Apply Waive / Discount amount now? (y/n): ");
                        if (wAns.equalsIgnoreCase("y")) {
                            double waive = input.readPositiveDouble("Waive / Discount Amount: ");
                            service.waiveFee(sn, waive);
                        }
                        service.syncStudentToAdminAndPortal(sn);
                        view.showMessage("Fees updated and synced to Admin & Student Portal.");
                        showBilling(service.findStudent(sn), sn);
                    }
                }
                case "3" -> {
                    view.showMessage("\n[Cashier Step 3] Waive / Discount Fee");
                    String sn = askStudentNo(preselectedStudentNo);
                    if (service.findStudent(sn) == null) {
                        view.showMessage("Student not found.");
                    } else {
                        double waive = input.readPositiveDouble("Waive / Discount Amount: ");
                        service.waiveFee(sn, waive);
                        service.syncStudentToAdminAndPortal(sn);
                        view.showMessage("Waive/discount applied and synced to Admin & Student Portal.");
                        showBilling(service.findStudent(sn), sn);
                    }
                }
                case "4" -> {
                    view.showMessage("\n[Cashier Step 4] Pay Tuition & Fees");
                    String sn = askStudentNo(preselectedStudentNo);
                    Student st = service.findStudent(sn);
                    if (st == null) {
                        view.showMessage("Student not found.");
                    } else {
                        showBilling(st, sn);
                        double amt = input.readPositiveDouble("Payment Amount: ");
                        String method = input.readNonEmpty("Payment Method (CASH / GCASH / BANK / CARD): ");
                        view.showMessage(service.recordPayment(sn, amt, method));
                        showBilling(st, sn);
                    }
                }
                case "5" -> {
                    view.showMessage("\n[Cashier Step 5] Student Payment History");
                    String sn = askStudentNo(preselectedStudentNo);
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

    private String askStudentNo(String preselected) {
        if (preselected != null && !preselected.isBlank()) {
            String typed = input.readLine("Student No. [Enter for " + preselected + "]: ");
            return typed.isEmpty() ? preselected : typed;
        }
        return input.readNonEmpty("Student No.: ");
    }

    private void handleStudent(User user) {
        Student st = service.resolveStudentForPortal(user.getLinkedId());
        String studNo = st != null ? st.getStudentNumber() : user.getLinkedId();
        while (true) {
            view.showStudentMenu(studNo);
            String c = input.readLine("Choice: ");
            switch (c) {
                case "1" -> {
                    view.showMessage("\n[Student Step 1] My Profile & Registration Info");
                    Student current = service.resolveStudentForPortal(user.getLinkedId());
                    if (current == null) {
                        view.showMessage("No student record found yet. Ask Registrar to enroll first.");
                    } else {
                        view.showMessage(current.toString());
                    }
                }
                case "2" -> {
                    view.showMessage("\n[Student Step 2] My Enrolled Subjects & Grades");
                    Student current = service.resolveStudentForPortal(user.getLinkedId());
                    String id = current != null ? current.getStudentNumber() : studNo;
                    var enrolledList = service.getEnrollmentsByStudent(id);
                    if (enrolledList.isEmpty()) {
                        String statusTag = current != null ? current.getStatus() : "NOT ENROLLED";
                        view.showMessage("Status: [" + statusTag + "] - No enrolled subjects yet for Student ID " + id + ".");
                    } else {
                        view.showMessage("Status: [ENROLLED]");
                        view.printEnrollments(enrolledList);
                    }
                }
                case "3" -> {
                    view.showMessage("\n[Student Step 3] My Assessment Slip & Tuition Balance");
                    Student current = service.resolveStudentForPortal(user.getLinkedId());
                    if (current == null) {
                        view.showMessage("No student record found yet.");
                    } else {
                        showBilling(current, current.getStudentNumber());
                    }
                }
                case "4" -> {
                    view.showMessage("\n[Student Step 4] Submit Shifting Request");
                    Student current = service.resolveStudentForPortal(user.getLinkedId());
                    String id = current != null ? current.getStudentNumber() : studNo;
                    String target = input.readNonEmpty("Target Course (e.g. BSCS): ");
                    String reason = input.readNonEmpty("Reason for shifting: ");
                    String result = service.submitShiftingRequest(id, target, reason);
                    view.showMessage("Shifting request status: OKAY / NADAWAT NA. " + result);
                }
                case "5" -> {
                    view.showMessage("\n[Student Step 5] My Shifting Requests");
                    Student current = service.resolveStudentForPortal(user.getLinkedId());
                    String id = current != null ? current.getStudentNumber() : studNo;
                    var myReqs = service.getShiftingRequestsByStudent(id);
                    if (myReqs.isEmpty()) {
                        view.showMessage("No shifting requests found.");
                    } else {
                        for (var req : myReqs) {
                            view.showMessage(req.toString() + " | OKAY / NADAWAT NA");
                        }
                    }
                }
                case "0" -> {
                    view.showMessage("Logging out from Student Portal...");
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
                    view.showMessage("Logging out from Instructor Portal...");
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