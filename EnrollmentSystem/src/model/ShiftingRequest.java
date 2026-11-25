package model;

public class ShiftingRequest {
    private final String requestId;
    private final String studentNumber;
    private final String currentCourse;
    private final String targetCourse;
    private final String reason;
    private String status; // PENDING, APPROVED, REJECTED

    public ShiftingRequest(String requestId, String studentNumber, String currentCourse, String targetCourse, String reason, String status) {
        this.requestId = requestId;
        this.studentNumber = studentNumber;
        this.currentCourse = currentCourse;
        this.targetCourse = targetCourse;
        this.reason = reason;
        this.status = status;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public String getCurrentCourse() {
        return currentCourse;
    }

    public String getTargetCourse() {
        return targetCourse;
    }

    public String getReason() {
        return reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String toDataString() {
        return escape(requestId) + "|" + escape(studentNumber) + "|" + escape(currentCourse) + "|"
                + escape(targetCourse) + "|" + escape(reason) + "|" + escape(status);
    }

    public static ShiftingRequest fromDataString(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 6) return null;
        return new ShiftingRequest(unescape(parts[0]), unescape(parts[1]), unescape(parts[2]),
                unescape(parts[3]), unescape(parts[4]), unescape(parts[5]));
    }

    @Override
    public String toString() {
        return String.format("[%s] Student: %s | %s -> %s | Status: %s | Reason: %s",
                requestId, studentNumber, currentCourse, targetCourse, status, reason);
    }

    private static String escape(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("|", "\\p");
    }

    private static String unescape(String s) {
        return s == null ? "" : s.replace("\\p", "|").replace("\\\\", "\\");
    }
}