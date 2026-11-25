package model;

public class Payment {
    private final String receiptId;
    private final String studentNumber;
    private final double amount;
    private final String method;
    private final String date;

    public Payment(String receiptId, String studentNumber, double amount, String method, String date) {
        this.receiptId = receiptId;
        this.studentNumber = studentNumber;
        this.amount = amount;
        this.method = method;
        this.date = date;
    }

    public String getReceiptId() { return receiptId; }
    public String getStudentNumber() { return studentNumber; }
    public double getAmount() { return amount; }
    public String getMethod() { return method; }
    public String getDate() { return date; }

    public String toDataString() {
        return escape(receiptId) + "|" + escape(studentNumber) + "|" + amount + "|"
                + escape(method) + "|" + escape(date);
    }

    public static Payment fromDataString(String line) {
        if (line == null || line.isBlank()) return null;
        String[] parts = line.split("\\|", -1);
        if (parts.length < 5) return null;
        try {
            return new Payment(
                    unescape(parts[0]),
                    unescape(parts[1]),
                    Double.parseDouble(parts[2].trim()),
                    unescape(parts[3]),
                    unescape(parts[4])
            );
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    @Override
    public String toString() {
        return String.format("[%s] Student: %s | Amount: %.2f | Method: %s | Date: %s",
                receiptId, studentNumber, amount, method, date);
    }

    private static String escape(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("|", "\\p");
    }

    private static String unescape(String s) {
        return s == null ? "" : s.replace("\\p", "|").replace("\\\\", "\\");
    }
}