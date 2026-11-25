package model;

public class Student {
    private final String studentNumber;
    private final String fullName;
    private String course;
    private int yearLevel;
    private String studentType;
    private String status;

    public Student(String studentNumber, String fullName, String course, int yearLevel, String status) {
        this(studentNumber, fullName, course, yearLevel, "REGULAR", status);
    }

    public Student(String studentNumber, String fullName, String course, int yearLevel, String studentType, String status) {
        this.studentNumber = studentNumber;
        this.fullName = fullName;
        this.course = course;
        this.yearLevel = yearLevel;
        this.studentType = (studentType == null || studentType.isBlank()) ? "REGULAR" : studentType.trim().toUpperCase();
        this.status = (status == null || status.isBlank()) ? "ENROLLED" : status.trim().toUpperCase();
    }

    public String getStudentNumber() { return studentNumber; }
    public String getFullName() { return fullName; }
    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }
    public int getYearLevel() { return yearLevel; }
    public void setYearLevel(int yearLevel) { this.yearLevel = yearLevel; }
    public String getStudentType() { return studentType; }
    public void setStudentType(String studentType) { this.studentType = studentType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String toDataString() {
        return escape(studentNumber) + "|" + escape(fullName) + "|" + escape(course) + "|"
                + yearLevel + "|" + escape(studentType) + "|" + escape(status);
    }

    public static Student fromDataString(String line) {
        if (line == null || line.isBlank()) return null;
        String[] parts = line.split("\\|", -1);
        try {
            if (parts.length == 5) {
                return new Student(unescape(parts[0]), unescape(parts[1]), unescape(parts[2]),
                        Integer.parseInt(parts[3].trim()), "REGULAR", unescape(parts[4]));
            }
            if (parts.length >= 6) {
                return new Student(unescape(parts[0]), unescape(parts[1]), unescape(parts[2]),
                        Integer.parseInt(parts[3].trim()), unescape(parts[4]), unescape(parts[5]));
            }
        } catch (NumberFormatException ignored) {
            return null;
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | %s - Year %d (%s) | Status: %s",
                studentNumber, fullName, course, yearLevel, studentType, status);
    }

    private static String escape(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("|", "\\p");
    }

    private static String unescape(String s) {
        return s == null ? "" : s.replace("\\p", "|").replace("\\\\", "\\");
    }
}


