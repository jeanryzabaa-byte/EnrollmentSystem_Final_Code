package view;

import java.util.List;

public class ConsoleView {
    private static final String LINE = "====================================================";

    public void showBanner() { System.out.println(LINE + "\n      STUDENT ENROLLMENT & ASSESSMENT SYSTEM\n" + LINE); }
    public void showMainMenu() { System.out.println("\n--- MAIN MENU ---\n1. Login\n2. Exit"); }
    public void showMessage(String msg) { System.out.println(msg); }

    public void showAdminMenu() {
        System.out.println("\n================ ADMIN MODULE ================\n1. Add User Account\n2. View Added Users\n3. View Subjects & Assigned Instructors\n4. View Enrolled Students & Instructors\n0. Logout\n==============================================");
    }

    public void showRegistrarMenu() {
        System.out.println("\n============== REGISTRAR MODULE ==============\n1. Enroll Regular Student (with Subjects)\n2. Enroll Transferee Student (with Subjects)\n3. View All Students\n4. Add Subject\n5. View All Subjects\n6. Shifting Requests (Fill Up / View & Process)\n0. Logout\n==============================================");
    }

    public void showCashierMenu() {
        System.out.println("\n========== CASHIERING & TUITION MODULE =========\n1. Set / Update Lab Fee & Miscellaneous Fee\n2. Apply Discount / Scholarship / Fee Waiver\n3. Record Tuition Payment\n4. View Assessment Slip & Tuition Balance\n5. View Student Payment History\n6. View Students Record\n7. View Fees\n0. Logout\n================================================");
    }

    public void showStudentMenu(String linkedId) {
        System.out.println("\n=========== STUDENT PORTAL (ID: " + linkedId + ") ===========\n1. My Profile & Registration Info\n2. My Enrolled Subjects & Grades\n3. My Assessment Slip & Tuition Balance\n4. Submit Shifting Request\n5. My Shifting Requests\n0. Logout\n================================================");
    }

    public void showInstructorMenu(String instructorId) {
        System.out.println("\n=========== INSTRUCTOR PORTAL (" + instructorId + ") ==========\n1. My Assigned Subjects\n2. View Class Roster & Encode Grade\n0. Logout\n==================================================");
    }

    public void printUsers(List<?> list) { print(list, "No users found."); }
    public void printStudents(List<?> list) { print(list, "No student records found."); }
    public void printSubjects(List<?> list) { print(list, "No subjects found."); }
    public void printEnrollments(List<?> list) { print(list, "No enrollment records found."); }
    public void printPayments(List<?> list) { print(list, "No payment records found."); }
    public void printShiftingRequests(List<?> list) { print(list, "No shifting requests found."); }

    private void print(List<?> list, String empty) {
        if (list == null || list.isEmpty()) System.out.println(empty);
        else list.forEach(System.out::println);
    }

    public void printDetailedBilling(Object student, double tuition, double lab, double misc,
                                     double waived, double total, double paid, double balance) {
        System.out.printf("%s%n              STUDENT ASSESSMENT SLIP%n%s%nStudent            : %s%nTuition Fee        : %.2f%nLaboratory Fees    : %.2f%nGeneral/Misc Fees  : %.2f%nLess Disc/Waived   : -%.2f%n----------------------------------------------------%nTotal Assessment   : %.2f%nTotal Paid         : %.2f%nRemaining Balance  : %.2f%n%s%n",
                LINE, LINE, student, tuition, lab, misc, waived, total, paid, balance, LINE);
    }
}