package view;

import model.EnrollmentRecord;
import model.Subject;

import java.util.List;

public class EnrollmentMenu {
    public void showEnrollmentOptions() {
        System.out.println("\n--- ENROLLMENT OPTIONS ---");
        System.out.println("1. Enroll Student in Subject");
        System.out.println("2. View Enrollments by Student");
        System.out.println("3. View Roster by Subject");
        System.out.println("0. Back");
    }

    public void printAvailableSubjects(List<Subject> subjects) {
        System.out.println("\n--- AVAILABLE SUBJECTS ---");
        if (subjects.isEmpty()) {
            System.out.println("No subjects available.");
            return;
        }
        for (Subject s : subjects) {
            System.out.println("  * " + s);
        }
    }

    public void printStudentEnrollments(String studentNumber, List<EnrollmentRecord> records) {
        System.out.println("\n--- ENROLLMENTS FOR STUDENT: " + studentNumber + " ---");
        if (records.isEmpty()) {
            System.out.println("No enrollment records found.");
            return;
        }
        for (EnrollmentRecord r : records) {
            System.out.println("  * " + r);
        }
    }
}