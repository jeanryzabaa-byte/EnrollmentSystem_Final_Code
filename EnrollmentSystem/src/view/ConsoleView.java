package view;

import model.EnrollmentRecord;
import model.Payment;
import model.ShiftingRequest;
import model.Student;
import model.Subject;
import model.User;

import java.util.List;

import java.util.List;

public class ConsoleView {
    public void showBanner() {
        System.out.println("====================================================");
        System.out.println("     ENROLLMENT MANAGEMENT SYSTEM (COMBINED)");
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
        System.out.println("1. Add Student Record");
        System.out.println("2. View All Students");
        System.out.println("3. Enroll Student in Subject");
        System.out.println("4. View Student Enrollments");
        System.out.println("5. Add Subject");
        System.out.println("6. View & Process Shifting Requests");
        System.out.println("0. Logout");
    }

    public void showCashierMenu() {
        System.out.println("\n--- CASHIER MENU ---");
        System.out.println("1. Check Student Assessment & Balance");
        System.out.println("2. Record Payment");
        System.out.println("3. View Student Payment History");
        System.out.println("0. Logout");
    }

    public void showStudentMenu(String linkedId) {
        System.out.println("\n--- STUDENT MENU (Linked ID: " + linkedId + ") ---");
        System.out.println("1. My Profile");
        System.out.println("2. My Enrolled Subjects & Grades");
        System.out.println("3. My Tuition & Balance");
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
        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }
        for (User u : users) {
            System.out.println("- " + u.getUsername() + " | Role: " + u.getRole() + " | LinkedID: " + u.getLinkedId());
        }
    }

    public void printStudents(List<Student> students) {
        if (students.isEmpty()) {
            System.out.println("No student records found.");
            return;
        }
        students.forEach(System.out::println);
    }

    public void printSubjects(List<Subject> subjects) {
        if (subjects.isEmpty()) {
            System.out.println("No subjects found.");
            return;
        }
        subjects.forEach(System.out::println);
    }

    public void printEnrollments(List<EnrollmentRecord> records) {
        if (records.isEmpty()) {
            System.out.println("No enrollment records found.");
            return;
        }
        records.forEach(System.out::println);
    }

    public void printPayments(List<Payment> payments) {
        if (payments.isEmpty()) {
            System.out.println("No payment records found.");
            return;
        }
        payments.forEach(System.out::println);
    }

    public void printShiftingRequests(List<ShiftingRequest> requests) {
        if (requests.isEmpty()) {
            System.out.println("No shifting requests found.");
            return;
        }
        requests.forEach(System.out::println);
    }

    public void printBillingSummary(Student student, double total, double paid, double balance) {
        System.out.printf("Student: %s (%s)%nTotal Assessment: %.2f%nTotal Paid: %.2f%nRemaining Balance: %.2f%n",
                student.getFullName(), student.getStudentNumber(), total, paid, balance);
    }
}