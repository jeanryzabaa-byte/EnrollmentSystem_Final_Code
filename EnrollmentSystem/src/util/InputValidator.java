package util;

import java.util.Scanner;

public class InputValidator {
    private final Scanner scanner;

    public InputValidator(Scanner scanner) {
        this.scanner = scanner;
    }

    public String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public String readNonEmpty(String prompt) {
        while (true) {
            String val = readLine(prompt);
            if (!val.isEmpty()) return val;
            System.out.println("Input cannot be empty. Please try again.");
        }
    }

    public int readInt(String prompt, int min, int max) {
        while (true) {
            String raw = readLine(prompt);
            try {
                int num = Integer.parseInt(raw);
                if (num >= min && num <= max) return num;
                System.out.println("Please enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please try again.");
            }
        }
    }

    public double readPositiveDouble(String prompt) {
        while (true) {
            String raw = readLine(prompt);
            try {
                double val = Double.parseDouble(raw);
                if (val >= 0) return val;
                System.out.println("Amount must be 0 or greater.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid amount. Please enter a valid number.");
            }
        }
    }
}