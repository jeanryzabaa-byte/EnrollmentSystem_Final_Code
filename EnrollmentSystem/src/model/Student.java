package model;

import java.util.ArrayList;
import java.util.List;

public class Student {
    private String id;
    private String studentNumber;
    private String fullName;
    private String course;
    private String yearLevel;
    private String enrollmentStatus;
    private List<Subject> enrolledSubjects = new ArrayList<>();

    public Student() {
        this.enrollmentStatus = "NOT ENROLLED";
    }

    public Student(String id, String fullName, String course, String yearLevel, String enrollmentStatus) {
        this.id = id;
        this.studentNumber = id;
        this.fullName = fullName;
        this.course = course;
        this.yearLevel = yearLevel;
        this.enrollmentStatus = (enrollmentStatus == null || enrollmentStatus.isBlank())
                ? "NOT ENROLLED"
                : enrollmentStatus.toUpperCase();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        if (this.studentNumber == null || this.studentNumber.isBlank()) {
            this.studentNumber = id;
        }
    }

    public String getStudentNumber() {
        if (studentNumber != null && !studentNumber.isBlank()) {
            return studentNumber;
        }
        return id;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getYearLevel() {
        return yearLevel;
    }

    public void setYearLevel(String yearLevel) {
        this.yearLevel = yearLevel;
    }

    public List<Subject> getEnrolledSubjects() {
        return enrolledSubjects;
    }

    public void setEnrolledSubjects(List<Subject> enrolledSubjects) {
        this.enrolledSubjects = (enrolledSubjects != null) ? enrolledSubjects : new ArrayList<>();
    }

    public void addSubject(Subject subject) {
        if (subject != null) {
            this.enrolledSubjects.add(subject);
        }
    }

    public String getEnrollmentStatus() {
        if (enrollmentStatus != null && !enrollmentStatus.isBlank()) {
            return enrollmentStatus.toUpperCase();
        }
        if (enrolledSubjects != null && !enrolledSubjects.isEmpty()) {
            return "ENROLLED";
        }
        return "NOT ENROLLED";
    }

    public void setEnrollmentStatus(String enrollmentStatus) {
        this.enrollmentStatus = (enrollmentStatus == null || enrollmentStatus.isBlank())
                ? "NOT ENROLLED"
                : enrollmentStatus.toUpperCase();
    }
}