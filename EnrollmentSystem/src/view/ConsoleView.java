package view;

import model.*;
import java.util.List;

public class ConsoleView {
    public void showBanner() {
        System.out.println("====================================================");
        System.out.println("        ENROLLMENT MANAGEMENT SYSTEM");
        System.out.println("====================================================");
    }

    public void showMainMenu() {
        System.out.println("\n1. Login");
        System.out.println("2. Exit");
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

    public void printUsers(List<User> users) {
        if (users.isEmpty()) { System.out.println("No users found."); return; }
        for (User u : users) {
            System.out.println("- " + u.getUsername() + " | Role: " + u.getRole() + " | LinkedID: " + u.getLinkedId());
        }
    }

    public void printStudents(List<Student> students) {
        if (students.isEmpty()) { System.out.println("No student records found."); return; }
        for (Student s : students) System.out.println(s);
    }

    public void printSubjects(List<Subject> subjects) {
        if (subjects.isEmpty()) { System.out.println("No subjects found."); return; }
        for (Subject s : subjects) System.out.println(s);
    }

    public void printEnrollments(List<EnrollmentRecord> records) {
        if (records.isEmpty()) { System.out.println("No enrollment records found."); return; }
        for (EnrollmentRecord r : records) System.out.println(r);
    }

    public void printPayments(List<Payment> payments) {
        if (payments.isEmpty()) { System.out.println("No payment records found."); return; }
        for (Payment p : payments) System.out.println(p);
    }

    public void printShiftingRequests(List<ShiftingRequest> requests) {
        if (requests.isEmpty()) { System.out.println("No shifting requests found."); return; }
        for (ShiftingRequest sr : requests) System.out.println(sr);
    }

    public void printDetailedBilling(Student student, double tuition, double labFee, double miscFee,
                                     double waived, double totalAssessment, double paid, double balance) {
        System.out.println("----------------------------------------------------");
        System.out.printf("Student           : %s (%s) [%s]%n",
                student.getFullName(), student.getStudentNumber(), student.getStudentType());
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