package view;

import java.util.List;
import model.ShiftingRequest;
import model.Student;

public class ConsoleView {

    public void showMessage(String message) {
        System.out.println(message);
    }

    public void printStudentEnrollments(List<Student> students) {
        System.out.println("\n================ ALL STUDENTS & ENROLLMENT STATUS ================");
        if (students == null || students.isEmpty()) {
            System.out.println("No student records found. (Status: NOT ENROLLED)");
            System.out.println("==================================================================");
            return;
        }

        for (Student s : students) {
            String status = s.getEnrollmentStatus();
            if (status == null || status.isBlank()) {
                status = "NOT ENROLLED";
            }

            System.out.println(
                "ID: " + safe(s.getId()) +
                " | Name: " + safe(s.getFullName()) +
                " | Course: " + safe(s.getCourse()) +
                " | Year: " + safe(s.getYearLevel()) +
                " | Status: " + status
            );
        }
        System.out.println("==================================================================");
    }

    public void printShiftingRequests(List<?> requests) {
        System.out.println("\n=== SHIFTING REQUESTS ===");
        if (requests == null || requests.isEmpty()) {
            System.out.println("No shifting requests.");
            return;
        }
        for (Object req : requests) {
            if (req instanceof ShiftingRequest) {
                System.out.println(req.toString());
            } else {
                System.out.println(String.valueOf(req));
            }
        }
    }

    private String safe(String value) {
        return (value == null || value.isBlank()) ? "N/A" : value;
    }
}