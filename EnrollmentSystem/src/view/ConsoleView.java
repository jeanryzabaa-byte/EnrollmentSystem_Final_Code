package view;

import java.util.List;

public class ConsoleView {
    private static final String LINE = "====================================================";

    public void showBanner() {
        System.out.println(LINE + "\n      STUDENT ENROLLMENT & ASSESSMENT SYSTEM\n" + LINE);
    }

    public void showMainMenu() {
        System.out.println("\n--- MAIN MENU ---\n1. Login\n2. Exit");
    }

    public void showMessage(String msg) { System.out.println(msg); }

    public void showAdminMenu() {
        System.out.println("\n================ ADMIN MODULE ================");
        System.out.println("1. Add User Account\n2. View Added Users\n3. View Subjects & Assigned Instructors");
        System.out.println("4. View Enrolled Students & Instructors\n0. Logout\n==============================================");
    }

    public void showRegistrarMenu() {
        System.out.println("\n============== REGISTRAR MODULE ==============");
        System.out.println("1. Enroll Regular Student (with Subjects)\n2. Enroll Transferee Student (with Subjects)");
        System.out.println("3. View All Students\n4. View Student Enrollments\n5. Add Subject");
        System.out.println("6. View All Subjects\n7. Shifting Requests (Fill Up / View & Process)\n0. Logout\n==============================================");
    }

    public void showCashierMenu() {
        System.out.println("\n========== CASHIERING & TUITION MODULE =========");
        System.out.println("1. Set / Update Lab Fee & Miscellaneous Fee");
        System.out.println("2. Apply Discount / Scholarship / Fee Waiver");
        System.out.println("3. Record Tuition Payment");
        System.out.println("4. View Assessment Slip & Tuition Balance");
        System.out.println("5. View Student Payment History");
        System.out.println("0. Logout");
        System.out.println("================================================");
    }

    public void showStudentMenu(String linkedId) {
        System.out.println("\n=========== STUDENT PORTAL (ID: " + linkedId + ") ===========");
        System.out.println("1. My Profile & Registration Info\n2. My Enrolled Subjects & Grades");
        System.out.println("3. My Assessment Slip & Tuition Balance\n4. Submit Shifting Request");
        System.out.println("5. My Shifting Requests\n0. Logout\n================================================");
    }

    public void showInstructorMenu(String instructorId) {
        System.out.println("\n=========== INSTRUCTOR PORTAL (" + instructorId + ") ==========");
        System.out.println("1. My Assigned Subjects\n2. View Class Roster & Encode Grade\n0. Logout\n==================================================");
    }

    public void printUsers(List<?> users) { printList(users, "No users found."); }
    public void printStudents(List<?> students) { printList(students, "No student records found."); }
    public void printSubjects(List<?> subjects) { printList(subjects, "No subjects found."); }
    public void printEnrollments(List<?> records) { printList(records, "No enrollment records found."); }
    public void printPayments(List<?> payments) { printList(payments, "No payment records found."); }
    public void printShiftingRequests(List<?> requests) { printList(requests, "No shifting requests found."); }

    private void printList(List<?> items, String emptyMsg) {
        if (items == null || items.isEmpty()) {
            System.out.println(emptyMsg);
            return;
        }
        items.forEach(System.out::println);
    }

    public void printDetailedBilling(Object student, double tuition, double labFee, double miscFee,
                                     double waived, double totalAssessment, double paid, double balance) {
        System.out.println(LINE + "\n              STUDENT ASSESSMENT SLIP\n" + LINE);
        System.out.printf("Student            : %s%nTuition Fee        : %.2f%nLaboratory Fees    : %.2f%nGeneral/Misc Fees  : %.2f%nLess Disc/Waived   : -%.2f%n",
                student, tuition, labFee, miscFee, waived);
        System.out.println("----------------------------------------------------");
        System.out.printf("Amount Due         : %.2f%nTotal Paid         : %.2f%nRemaining Balance  : %.2f%n",
                totalAssessment, paid, balance);
        System.out.println(LINE);
    }
}