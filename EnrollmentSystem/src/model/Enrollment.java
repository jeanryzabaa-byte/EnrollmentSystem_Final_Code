package model;

public abstract class Enrollment {
    private String studentId;
    private String courseCode;

    public Enrollment(String studentId, String courseCode) {
        this.studentId = studentId;
        this.courseCode = courseCode;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public abstract void enroll();
}