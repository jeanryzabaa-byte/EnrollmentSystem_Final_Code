package view;

import java.util.List;

public class ConsoleView {
    public void showBanner() {
        System.out.println("====================================================");
        System.out.println("      STUDENT ENROLLMENT & ASSESSMENT SYSTEM");
        System.out.println("====================================================");
    }

    public void showMainMenu() {
        System.out.println("\n--- MAIN MENU ---");
        System.out.println("1. Login");
        System.out.println("2. Exit");
    }

    public void showMessage(String msg) {
        System.out.println(msg);
    }

    public void showAdminMenu() {
        System.out.println("\n================ ADMIN MODULE ================");
        System.out.println("1. Add User Account");
        System.out.println("2. View Added Users");
        System.out.println("3. View Subjects & Assigned Instructors");
        System.out.println("4. View Enrolled Students & Instructors");
        System.out.println("0. Logout");
        System.out.println("==============================================");
    }

    public void showRegistrarMenu() {
        System.out.println("\n============== REGISTRAR MODULE ==============");
        System.out.println("1. Enroll Regular Student (with Subjects)");
        System.out.println("2. Enroll Transferee Student (with Subjects)");
        System.out.println("3. View All Students");
        System.out.println("4. View Student Enrollments");
        System.out.println("5. Add Subject");
        System.out.println("6. Shifting Requests (Fill Up / View & Process)");
        System.out.println("0. Logout");
        System.out.println("==============================================");
    }

    public void showCashierMenu() {
        System.out.println("\n========== CASHIERING & TUITION MODULE =========");
        System.out.println("1. View Assessment Slip & Tuition Balance");
        System.out.println("2. Set / Update Lab Fee & Miscellaneous Fee");
        System.out.println("3. Apply Discount / Scholarship / Fee Waiver");
        System.out.println("4. Record Tuition Payment");
        System.out.println("5. View Student Payment History");
        System.out.println("0. Logout");
        System.out.println("================================================");
    }

    public void showStudentMenu(String linkedId) {
        System.out.println("\n=========== STUDENT PORTAL (ID: " + linkedId + ") ===========");
        System.out.println("1. My Profile & Registration Info");
        System.out.println("2. My Enrolled Subjects & Grades");
        System.out.println("3. My Assessment Slip & Tuition Balance");
        System.out.println("4. Submit Shifting Request");
        System.out.println("5. My Shifting Requests");
        System.out.println("0. Logout");
        System.out.println("================================================");
    }

    public void showInstructorMenu(String instructorId) {
        System.out.println("\n=========== INSTRUCTOR PORTAL (" + instructorId + ") ==========");
        System.out.println("1. My Assigned Subjects");
        System.out.println("2. View Class Roster & Encode Grade");
        System.out.println("0. Logout");
        System.out.println("==================================================");
    }

    public void printUsers(List<?> users) {
        printList(users, "No users found.");
    }

    public void printStudents(List<?> students) {
        printList(students, "No student records found.");
    }

    public void printSubjects(List<?> subjects) {
        printList(subjects, "No subjects found.");
    }

    public void printEnrollments(List<?> records) {
        printList(records, "No enrollment records found.");
    }

    public void printPayments(List<?> payments) {
        printList(payments, "No payment records found.");
    }

    public void printShiftingRequests(List<?> requests) {
        printList(requests, "No shifting requests found.");
    }

    private void printList(List<?> items, String emptyMsg) {
        if (items == null || items.isEmpty()) {
            System.out.println(emptyMsg);
            return;
        }
        for (Object item : items) {
            System.out.println(item);
        }
    }

    public void printDetailedBilling(Object student, double tuition, double labFee, double miscFee,
                                     double waived, double totalAssessment, double paid, double balance) {
        System.out.println("====================================================");
        System.out.println("              STUDENT ASSESSMENT SLIP");
        System.out.println("====================================================");
        System.out.println("Student            : " + student);
        System.out.printf("Tuition Fee        : %.2f%n", tuition);
        System.out.printf("Laboratory Fees    : %.2f%n", labFee);
        System.out.printf("General/Misc Fees  : %.2f%n", miscFee);
        System.out.printf("Less Disc/Waived   : -%.2f%n", waived);
        System.out.println("----------------------------------------------------");
        System.out.printf("Amount Due         : %.2f%n", totalAssessment);
        System.out.printf("Total Paid         : %.2f%n", paid);
        System.out.printf("Remaining Balance  : %.2f%n", balance);
        System.out.println("====================================================");
    }
}