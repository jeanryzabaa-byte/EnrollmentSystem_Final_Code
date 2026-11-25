package view;

import model.EnrollmentRecord;
import model.Payment;
import model.ShiftingRequest;
import model.Student;
import model.Subject;
import model.User;

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
        System.out.println("2. Register Account (Set Own Username & Password)");
        System.out.println("3. Exit");
    }

    public void showMessage(String msg) {
        System.out.println(msg);
    }

    public void showAdminMenu() {
        System.out.println("\n================ ADMIN MODULE ================");
        System.out.println("1. Add User Account");
        System.out.println("2. View All Users");
        System.out.println("3. Add Subject");
        System.out.println("4. View All Subjects");
        System.out.println("5. View All Students");
        System.out.println("0. Logout");
        System.out.println("==============================================");
    }

    public void showRegistrarMenu() {
        System.out.println("\n============== REGISTRAR MODULE ==============");
        System.out.println("1. Enroll Regular Student");
        System.out.println("2. Enroll Transferee Student");
        System.out.println("3. View All Students");
        System.out.println("4. Enroll Student in Subject");
        System.out.println("5. View Student Enrollments");
        System.out.println("6. Add Subject");
        System.out.println("7. View & Process Shifting Requests");
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
        System.out.println("\n=========== STUDENT PORTAL (" + linkedId + ") ===========");
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

    public void printUsers(List<User> users) {
        if (users == null || users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }
        for (User u : users) {
            System.out.println("- " + u.getUsername() + " | Role: " + u.getRole() + " | LinkedID: " + u.getLinkedId());
        }
    }

    public void printStudents(List<Student> students) {
        if (students == null || students.isEmpty()) {
            System.out.println("No student records found.");
            return;
        }
        for (Student s : students) System.out.println(s);
    }

    public void printSubjects(List<Subject> subjects) {
        if (subjects == null || subjects.isEmpty()) {
            System.out.println("No subjects found.");
            return;
        }
        for (Subject s : subjects) System.out.println(s);
    }

    public void printEnrollments(List<EnrollmentRecord> records) {
        if (records == null || records.isEmpty()) {
            System.out.println("No enrollment records found.");
            return;
        }
        for (EnrollmentRecord r : records) System.out.println(r);
    }

    public void printPayments(List<Payment> payments) {
        if (payments == null || payments.isEmpty()) {
            System.out.println("No payment records found.");
            return;
        }
        for (Payment p : payments) System.out.println(p);
    }

    public void printShiftingRequests(List<ShiftingRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            System.out.println("No shifting requests found.");
            return;
        }
        for (ShiftingRequest sr : requests) System.out.println(sr);
    }

    public void printDetailedBilling(Student student, double tuition, double labFee, double miscFee,
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