package view;

import java.util.Scanner;

public class EnrollmentMenu {
    private Scanner scanner;

    public EnrollmentMenu(Scanner scanner) {
        this.scanner = scanner;
    }

    public void displayMenu() {
        System.out.println("\n=== STUDENT ENROLLMENT SYSTEM ===");
        System.out.println("1. Register Student");
        System.out.println("2. Display All Student");
        System.out.println("3. Search Student");
        System.out.println("4. Update Student");
        System.out.println("5. Delete Student");
        System.out.println("6. Manage Subjects");
        System.out.println("7. Enroll Subject / Enrollment Record");
        System.out.println("8. Record Payment");
        System.out.println("9. Shifting Request");
        System.out.println("10. Manage / View Users");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");
    }

    public int getChoice() {
        while (!scanner.hasNextInt()) {
            System.out.print("Please enter a valid number: ");
            scanner.next();
        }
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline
        return choice;
    }

    public String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
}