package util;

import java.io.Console;
import java.io.IOException;
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

    public String readPassword(String prompt) {
        while (true) {
            Console console = System.console();
            if (console != null) {
                char[] pwd = console.readPassword(prompt);
                if (pwd != null) {
                    String val = new String(pwd).trim();
                    java.util.Arrays.fill(pwd, ' ');
                    if (!val.isEmpty()) return val;
                }
                System.out.println("Password cannot be empty. Please try again.");
                continue;
            }

            System.out.print(prompt);
            StringBuilder sb = new StringBuilder();
            try {
                while (true) {
                    int ch = System.in.read();
                    if (ch == -1 || ch == '\n') {
                        break;
                    }
                    if (ch == '\r') {
                        continue;
                    }
                    if (ch == 8 || ch == 127) { // Backspace
                        if (sb.length() > 0) {
                            sb.deleteCharAt(sb.length() - 1);
                            System.out.print("\b \b");
                        }
                    } else {
                        sb.append((char) ch);
                        System.out.print("*");
                    }
                }
                System.out.println();
                String val = sb.toString().trim();
                if (!val.isEmpty()) return val;
                System.out.println("Password cannot be empty. Please try again.");
            } catch (IOException e) {
                String fallback = scanner.nextLine().trim();
                if (!fallback.isEmpty()) return fallback;
                System.out.println("Password cannot be empty. Please try again.");
            }
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


