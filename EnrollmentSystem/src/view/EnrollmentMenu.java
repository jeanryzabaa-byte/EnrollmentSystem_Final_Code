package view;

import java.util.Scanner;

public class EnrollmentMenu {
    private final Scanner scanner = new Scanner(System.in);

    public void displayMenu() {
        System.out.println("\n=== STUDENT ENROLLMENT SYSTEM ===");
    }

    public int getChoice() {
        while (true) {
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Invalid choice. Enter a number: ");
            }
        }
    }

    public String readLine(String prompt) {
        if (!prompt.isEmpty()) {
            System.out.print(prompt);
        }
        return scanner.nextLine().trim();
    }
}
        