package view;

import java.util.List;

public class ConsoleView {
    public void showBanner() {
        System.out.println("====================================================");
        System.out.println("        ENROLLMENT MANAGEMENT SYSTEM");
        System.out.println("====================================================");
    }

    public void showMainMenu() {
        System.out.println("\n1. Login");
        System.out.println("2. Register Account (Set Own Username & Password)");
        System.out.println("3. Exit");
    }

    public void showMessage(String msg) {
        System.out.println(msg);
    }

    public void showAdminMenu() {
        System.out.println("\n--- ADMIN MENU ---");
        System.out.println("1. Add User Account");
        System.out.println("2. View All Users");
        System.out.println("3. Add Subject");
        System.out.println("4. View All Subjects");
        System.out.println("5. View All Students");
        System.out.println("0. Logout");
    }

    public void showRegistrarMenu() {
        System.out.println("\n--- REGISTRAR MENU ---");
        System.out.println("1. Enroll Regular Student");
        System.out.println("2. Enroll Transferee Student");
        System.out.println("3. View All Students");
        System.out.println("4. Enroll Student in Subject");
        System.out.println("5. View Student Enrollments");
        System.out.println("6. Add Subject");
        System.out.println("7. View & Process Shifting Requests");
        System.out.println("0. Logout");
    }

    public void showCashierMenu() {
        System.out.println("\n--- CASHIERING MENU ---");
        System.out.println("1. Check Student Assessment & Balance");
        System.out.println("2. Set / Update Lab Fee & Miscellaneous Fee");
        System.out.println("3. Waive Fee");
        System.out.println("4. Record Payment");
        System.out.println("5. View Student Payment History");
        System.out.println("0. Logout");
    }

    public void showStudentMenu(String linkedId) {
        System.out.println("\n--- STUDENT MENU (ID: " + linkedId + ") ---");
        System.out.println("1. My Profile");
        System.out.println("2. My Enrolled Subjects & Grades");
        System.out.println("3. My Tuition, Fees & Balance");
        System.out.println("4. Submit Shifting Request");
        System.out.println("5. My Shifting Requests");
        System.out.println("0. Logout");
    }

    public void showInstructorMenu(String instructorId) {
        System.out.println("\n--- INSTRUCTOR MENU (" + instructorId + ") ---");
        System.out.println("1. My Assigned Subjects");
        System.out.println("2. View Class Roster & Encode Grade");
        System.out.println("0. Logout");
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
        System.out.println("----------------------------------------------------");
        System.out.println("Student           : " + student);
        System.out.printf("Tuition Fee       : %.2f%n", tuition);
        System.out.printf("Laboratory Fee    : %.2f%n", labFee);
        System.out.printf("Miscellaneous Fee : %.2f%n", miscFee);
        System.out.printf("Waived Amount     : -%.2f%n", waived);
        System.out.printf("Total Assessment  : %.2f%n", totalAssessment);
        System.out.printf("Total Paid        : %.2f%n", paid);
        System.out.printf("Remaining Balance : %.2f%n", balance);
        System.out.println("----------------------------------------------------");
    }
}