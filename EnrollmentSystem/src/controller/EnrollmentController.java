package controller;

import java.util.ArrayList;
import java.util.List;
import model.*;
import service.EnrollmentService;
import view.EnrollmentMenu;

public class EnrollmentController {
    private final EnrollmentMenu menu;
    private final EnrollmentService service;

    private static final double TUITION_PER_UNIT = 150.00;
    private static final double DEFAULT_MISC_FEE = 4253.00;

    private final List<Student> students = new ArrayList<>();
    private final List<User> users = new ArrayList<>();
    private final List<Instructor> instructors = new ArrayList<>();
    private final List<Subject> subjects = new ArrayList<>();
    private final List<EnrollmentRecord> enrollmentRecords = new ArrayList<>();
    private final List<Payment> payments = new ArrayList<>();
    private final List<Shifting> shiftings = new ArrayList<>();

    public EnrollmentController(EnrollmentMenu menu, EnrollmentService service) {
        this.menu = menu;
        this.service = service;
        seedDefaults();
    }

    private void seedDefaults() {
        users.add(new User("admin", "1234", "System Admin", "Admin"));
        users.add(new User("registrar1", "1234", "Registrar Officer", "Registrar"));
        users.add(new User("cashier1", "1234", "Cashier Officer", "Cashier"));

        instructors.add(new Instructor("INS-01", "Saligue, E", "IT", "IT 205"));
        instructors.add(new Instructor("INS-02", "Cambaya, F", "IT", "IT 201"));
        instructors.add(new Instructor("INS-03", "Asma, C", "IT", "IT 203"));
    }

    public void start() {
        boolean running = true;
        while (running) {
            printMenu();
            int choice = menu.getChoice();

            switch (choice) {
                case 1:
                    registerStudent();
                    break;
                case 2:
                    displayStudents();
                    break;
                case 3:
                    searchStudent();
                    break;
                case 4:
                    addSubject();
                    displaySubjects();
                    break;
                case 5:
                    enrollSubject();
                    break;
                case 6:
                    shiftCourse();
                    displayShiftings();
                    break;
                case 7:
                    registrarValidate();
                    break;
                case 8:
                    cashiering();
                    break;
                case 9:
                    waiveStudentFees();
                    break;
                case 10:
                    manageInstructors();
                    break;
                case 11:
                    manageUsers();
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

    private void printMenu() {
        System.out.println("\n=== STUDENT ENROLLMENT SYSTEM ===");
        System.out.println("1. Register Student");
        System.out.println("2. Display All Students");
        System.out.println("3. Search Student");
        System.out.println("4. Add Subject & View Subjects");
        System.out.println("5. Enroll Subject & Print Summary");
        System.out.println("6. Shifting Request");
        System.out.println("7. Registrar (Approve / Validate)");
        System.out.println("8. Cashiering (Lab Fee, Misc Fee & Payment)");
        System.out.println("9. Waive Fees");
        System.out.println("10. Instructor Management");
        System.out.println("11. User Management");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");
    }

    private void registerStudent() {
        String studentNo = menu.readLine("Student No: ");
        String name = menu.readLine("Full Name: ");
        String course = menu.readLine("Course (e.g., BSIT): ");
        String major = menu.readLine("Major: ");
        String yearSection = menu.readLine("Year & Section: ");
        String status = menu.readLine("Status: ");
        String sex = menu.readLine("Sex: ");
        String birthDate = menu.readLine("Birth Date: ");

        students.add(new Student(studentNo, name, course, major, yearSection, status, sex, birthDate));
        System.out.println("Student registered!");
    }

    private void displayStudents() {
        if (students.isEmpty()) {
            System.out.println("No students registered.");
            return;
        }
        System.out.println("\n--- STUDENTS ---");
        for (Student s : students) {
            System.out.printf("%s | %s | %s (%s) | Registrar: %s | Waive: %.0f%%%n",
                    s.studentNo, s.name, s.course, s.yearSection,
                    s.isRegistrarApproved() ? "Approved" : "Pending", s.getWaiverPercent());
        }
    }

    private void searchStudent() {
        String studentNo = menu.readLine("Enter Student No: ");
        Student s = findStudent(studentNo);
        if (s == null) {
            System.out.println("Student not found.");
        } else {
            System.out.println("Found: " + s.studentNo + " - " + s.name + " (" + s.course + ")");
        }
    }

    private Student findStudent(String studentNo) {
        for (Student s : students) {
            if (s.studentNo.equalsIgnoreCase(studentNo)) {
                return s;
            }
        }
        return null;
    }

    private void addSubject() {
        String code = menu.readLine("Subject Code (e.g., IT 205L): ");
        if (findSubject(code) != null) {
            System.out.println("Subject already exists.");
            return;
        }
        String title = menu.readLine("Subject Title: ");
        int units = readIntSafe("Units: ");
        String labAns = menu.readLine("Has Lab Fee? (y/n): ");
        boolean hasLab = labAns.equalsIgnoreCase("y");
        double labFee = hasLab ? readDoubleSafe("Lab Fee (e.g., 1000): ") : 0.0;
        double miscFee = readDoubleSafe("Additional Misc Fee for subject (0 if none): ");

        subjects.add(new Subject(code, title, units, hasLab, labFee, miscFee));
        System.out.println("Subject added!");
    }

    private void displaySubjects() {
        if (subjects.isEmpty()) {
            System.out.println("No subjects yet.");
            return;
        }
        System.out.println("\n--- SUBJECTS ---");
        for (Subject sub : subjects) {
            System.out.printf("%-8s | %-28s | %d units | Lab: %.2f | Misc: %.2f%n",
                    sub.getSubjectCode(), sub.getSubjectTitle(), sub.getUnits(),
                    sub.getLabFee(), sub.getMiscFee());
        }
    }

    private Subject findSubject(String code) {
        for (Subject sub : subjects) {
            if (sub.getSubjectCode().equalsIgnoreCase(code)) {
                return sub;
            }
        }
        return null;
    }

    private void enrollSubject() {
        String id = menu.readLine("Enrollment ID: ");
        String studentId = menu.readLine("Student No: ");
        String subjectCode = menu.readLine("Subject Code: ");

        if (findSubject(subjectCode) == null) {
            System.out.println("Subject not found. Add it first in option 4.");
            return;
        }

        String semester = menu.readLine("Semester: ");
        String status = menu.readLine("Status (Enrolled): ");

        enrollmentRecords.add(new EnrollmentRecord(id, studentId, subjectCode, semester, status));
        System.out.println("Subject enrolled!");
        printSummary(studentId, semester);
    }

    private void shiftCourse() {
        String id = menu.readLine("Shifting ID: ");
        String studentId = menu.readLine("Student No: ");
        Student student = findStudent(studentId);
        String fromCourse = (student != null) ? student.course : menu.readLine("From Course: ");
        String toCourse = menu.readLine("To Course: ");
        String status = menu.readLine("Status (Approved / Pending): ");

        shiftings.add(new Shifting(id, studentId, fromCourse, toCourse, status));
        if (student != null && status.equalsIgnoreCase("Approved")) {
            student.course = toCourse;
            System.out.println("Course updated to " + toCourse + ".");
        }
        System.out.println("Shifting request saved.");
    }

    private void displayShiftings() {
        if (shiftings.isEmpty()) {
            System.out.println("No shifting records.");
            return;
        }
        System.out.println("\n--- SHIFTING RECORDS ---");
        for (Shifting sh : shiftings) {
            System.out.println(sh.getShiftingId() + " | " + sh.getStudentId()
                    + " | " + sh.getFromCourse() + " -> " + sh.getToCourse()
                    + " | " + sh.getStatus());
        }
    }

    private void registrarValidate() {
        String studentNo = menu.readLine("Student No for Registrar validation: ");
        Student s = findStudent(studentNo);
        if (s == null) {
            System.out.println("Student not found.");
            return;
        }
        String ans = menu.readLine("Approve registration? (y/n): ");
        s.setRegistrarApproved(ans.equalsIgnoreCase("y"));
        System.out.println("Registrar status updated: " + (s.isRegistrarApproved() ? "Approved" : "Pending"));
    }

    private void waiveStudentFees() {
        String studentNo = menu.readLine("Student No for fee waiver: ");
        Student s = findStudent(studentNo);
        if (s == null) {
            System.out.println("Student not found.");
            return;
        }
        double pct = readDoubleSafe("Waiver % (0 to 100): ");
        s.setWaiverPercent(pct);
        System.out.printf("Waiver updated to %.0f%% for %s.%n", s.getWaiverPercent(), s.studentNo);
    }

    private void cashiering() {
        String studentNo = menu.readLine("Student No for Cashiering: ");
        String semester = menu.readLine("Semester: ");
        printSummary(studentNo, semester);

        String payAns = menu.readLine("Record payment? (y/n): ");
        if (payAns.equalsIgnoreCase("y")) {
            String payId = menu.readLine("Payment ID: ");
            double amount = readDoubleSafe("Amount Paid: ");
            String date = menu.readLine("Date: ");
            String method = menu.readLine("Method (Cash / Waived / GCash): ");
            payments.add(new Payment(payId, studentNo, amount, date, method));
            System.out.println("Payment recorded!");
        }
    }

    private void manageInstructors() {
        System.out.println("\n--- INSTRUCTORS ---");
        for (Instructor ins : instructors) {
            System.out.println(ins.getInstructorId() + " | " + ins.getName()
                    + " | " + ins.getDepartment() + " | Subj: " + ins.getAssignedSubjectCode());
        }
        String ans = menu.readLine("Add instructor? (y/n): ");
        if (ans.equalsIgnoreCase("y")) {
            String id = menu.readLine("Instructor ID: ");
            String name = menu.readLine("Name: ");
            String dept = menu.readLine("Department: ");
            String subCode = menu.readLine("Assigned Subject Code: ");
            instructors.add(new Instructor(id, name, dept, subCode));
            System.out.println("Instructor added!");
        }
    }

    private void manageUsers() {
        System.out.println("\n--- USERS ---");
        for (User u : users) {
            System.out.println(u.getUsername() + " | " + u.getFullName() + " | " + u.getRole());
        }
        String ans = menu.readLine("Add user? (y/n): ");
        if (ans.equalsIgnoreCase("y")) {
            String user = menu.readLine("Username: ");
            String pass = menu.readLine("Password: ");
            String name = menu.readLine("Full Name: ");
            String role = menu.readLine("Role (Admin / Registrar / Cashier): ");
            users.add(new User(user, pass, name, role));
            System.out.println("User added!");
        }
    }

    private String getLatestShifting(String studentId) {
        for (int i = shiftings.size() - 1; i >= 0; i--) {
            Shifting sh = shiftings.get(i);
            if (sh.getStudentId().equalsIgnoreCase(studentId)) {
                return sh.getFromCourse() + " -> " + sh.getToCourse() + " (" + sh.getStatus() + ")";
            }
        }
        return "None";
    }

    private void printSummary(String studentId, String semester) {
        Student s = findStudent(studentId);
        String name = (s != null) ? s.name : "N/A";
        String courseYr = (s != null) ? (s.course + " - " + s.yearSection) : "N/A";
        String regStatus = (s != null && s.isRegistrarApproved()) ? "Approved" : "Pending";
        double waivePct = (s != null) ? s.getWaiverPercent() : 0.0;
        String shiftingInfo = getLatestShifting(studentId);

        int totalUnits = 0;
        double labFeeTotal = 0.0;
        double extraMiscFee = 0.0;
        List<Subject> enrolledSubs = new ArrayList<>();

        for (EnrollmentRecord er : enrollmentRecords) {
            if (er.getStudentId().equalsIgnoreCase(studentId)) {
                Subject sub = findSubject(er.getSubjectCode());
                if (sub != null) {
                    enrolledSubs.add(sub);
                    totalUnits += sub.getUnits();
                    labFeeTotal += sub.getLabFee();
                    extraMiscFee += sub.getMiscFee();
                }
            }
        }

        double tuitionFee = totalUnits * TUITION_PER_UNIT;
        double miscFee = (totalUnits > 0) ? (DEFAULT_MISC_FEE + extraMiscFee) : 0.0;
        double grossTotal = tuitionFee + labFeeTotal + miscFee;
        double waivedAmount = grossTotal * (waivePct / 100.0);
        double amountDue = grossTotal - waivedAmount;

        System.out.println("\n==================================================");
        System.out.println("      ENROLLMENT, REGISTRAR & CASHIER SUMMARY");
        System.out.println("==================================================");
        System.out.println("Student No   : " + studentId);
        System.out.println("Name         : " + name);
        System.out.println("Course / Yr  : " + courseYr);
        System.out.println("Semester     : " + semester);
        System.out.println("Registrar    : " + regStatus);
        System.out.println("Shifting     : " + shiftingInfo);
        System.out.println("\nEnrolled Subjects:");
        for (Subject sub : enrolledSubs) {
            System.out.printf("  - %-8s | %-28s | %d units%n",
                    sub.getSubjectCode(), sub.getSubjectTitle(), sub.getUnits());
        }
        System.out.println("--------------------------------------------------");
        System.out.println("Total Units  : " + totalUnits);
        System.out.printf("Tuition Fee  : %.2f%n", tuitionFee);
        System.out.printf("Lab Fee      : %.2f%n", labFeeTotal);
        System.out.printf("Misc Fee     : %.2f%n", miscFee);
        System.out.printf("Waived (%.0f%%): -%.2f%n", waivePct, waivedAmount);
        System.out.println("--------------------------------------------------");
        System.out.printf("AMOUNT DUE   : %.2f%n", amountDue);
        System.out.println("==================================================");
    }

    private int readIntSafe(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(menu.readLine(prompt));
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Try again.");
            }
        }
    }

    private double readDoubleSafe(String prompt) {
        while (true) {
            try {
                return Double.parseDouble(menu.readLine(prompt));
            } catch (NumberFormatException e) {
                System.out.println("Invalid amount. Try again.");
            }
        }
    }
}