package enrollmentsystem.model.payment;

import java.time.LocalDateTime;

public class Payment {
    private final String paymentId;
    private final String studentNumber;
    private final double amount;
    private final String method; // CASH, GCASH, BANK, CARD
    private final String timestamp;

    public Payment(String paymentId, String studentNumber, double amount, String method, String timestamp) {
        this.paymentId = paymentId;
        this.studentNumber = studentNumber;
        this.amount = amount;
        this.method = method;
        this.timestamp = (timestamp == null || timestamp.isBlank()) ? LocalDateTime.now().toString() : timestamp;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public double getAmount() {
        return amount;
    }

    public String getMethod() {
        return method;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String toDataString() {
        return escape(paymentId) + "|" + escape(studentNumber) + "|" + amount + "|" + escape(method) + "|" + escape(timestamp);
    }

    public static Payment fromDataString(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 5) return null;
        try {
            double amt = Double.parseDouble(parts[2].trim());
            return new Payment(unescape(parts[0]), unescape(parts[1]), amt, unescape(parts[3]), unescape(parts[4]));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return String.format("[%s] Student: %s | Amount: %.2f | Method: %s | Date: %s",
                paymentId, studentNumber, amount, method, timestamp);
    }

    private static String escape(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("|", "\\p");
    }

    private static String unescape(String s) {
        return s == null ? "" : s.replace("\\p", "|").replace("\\\\", "\\");
    }
}