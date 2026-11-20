package view;

import java.util.ArrayList;
import java.util.Scanner;
import model.Student;

public class EnrollmentMenu {
    private final Scanner scanner;

    public EnrollmentMenu(Scanner scanner) {
        this.scanner = scanner;
    }

    public void displayMenu() {
        System.out.println("\n----------------------------------------");
        System.out.println("      *** STUDENT MANAGEMENT SYSTEM ***   ");
        System.out.println("----------------------------------------");
        System.out.println("1. Add New Student");
        System.out.println("2. Display All Student");
        System.out.println("3. Search Student");
        System.out.println("4. Update Student");
        System.out.println("5. Delete Student");
        System.out.println("6. Exit");
        System.out.println("----------------------------------------");
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
            System.out.println("----------------------------------------");
        }
    }

    public void displayMessage(String message) {
        System.out.println(message);
    }
}