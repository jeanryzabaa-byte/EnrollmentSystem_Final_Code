package view;

import java.util.Scanner;
import java.util.ArrayList;
import model.Student;

public class EnrollmentMenu {
    private final Scanner scanner;

    public EnrollmentMenu(Scanner scanner) {
        this.scanner = scanner;
    }

    public void displayMenu() {
        System.out.println("\n================================");
        System.out.println("     ENROLLMENT MANAGEMENT");
        System.out.println("================================");
        System.out.println("[1] Add Student");
        System.out.println("[2] View Records");
        System.out.println("[3] Exit");
        System.out.println("================================");
        System.out.print("Enter your choice: ");
    }

    public String getInput(String message) {
        System.out.print(message);
        return scanner.nextLine().trim();
    }

    public void displayStudents(ArrayList<Student> students) {
        if (students.isEmpty()) {
            System.out.println("\nNo student records found.");
            return;
        }

        System.out.println("\n========== STUDENT RECORDS ==========");
        for (Student student : students) {
            student.displayInfo();
        }
    }

    public void displayMessage(String message) {
        System.out.println(message);
    }
}
