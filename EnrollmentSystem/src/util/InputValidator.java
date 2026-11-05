package util;

public class InputValidator {
    public static boolean isValid(String input) {
        return input != null
                && !input.trim().isEmpty()
                && !input.contains("|");
    }
}
