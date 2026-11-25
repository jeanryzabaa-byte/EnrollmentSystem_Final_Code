package model;

public class EnrollmentRecord {
    private final String recordId;
    private final String studentNumber;
    private final String subjectCode;
    private final String semester;
    private String status;
    private String grade;

    public EnrollmentRecord(String recordId, String studentNumber, String subjectCode,
                            String semester, String status, String grade) {
        this.recordId = recordId;
        this.studentNumber = studentNumber;
        this.subjectCode = subjectCode;
        this.semester = semester;
        this.status = status;
        this.grade = grade;
    }

    public String getRecordId() { return recordId; }
    public String getStudentNumber() { return studentNumber; }
    public String getSubjectCode() { return subjectCode; }
    public String getSemester() { return semester; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public String toDataString() {
        return escape(recordId) + "|" + escape(studentNumber) + "|" + escape(subjectCode) + "|"
                + escape(semester) + "|" + escape(status) + "|" + escape(grade);
    }

    public static EnrollmentRecord fromDataString(String line) {
        if (line == null || line.isBlank()) return null;
        String[] parts = line.split("\\|", -1);
        if (parts.length < 6) return null;
        return new EnrollmentRecord(
                unescape(parts[0]), unescape(parts[1]), unescape(parts[2]),
                unescape(parts[3]), unescape(parts[4]), unescape(parts[5])
        );
    }

    @Override
    public String toString() {
        return String.format("[%s] Student: %s | Subject: %s | Period: %s | Grade: %s (%s)",
                recordId, studentNumber, subjectCode, semester, grade, status);
    }

    private static String escape(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("|", "\\p");
    }

    private static String unescape(String s) {
        return s == null ? "" : s.replace("\\p", "|").replace("\\\\", "\\");
    }
}