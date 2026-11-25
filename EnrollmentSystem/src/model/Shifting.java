package model;

public class Shifting {
    private String shiftingId;
    private String studentId;
    private String fromCourse;
    private String toCourse;
    private String status;

    public Shifting() {
    }

    public Shifting(String shiftingId, String studentId, String fromCourse, String toCourse, String status) {
        this.shiftingId = shiftingId;
        this.studentId = studentId;
        this.fromCourse = fromCourse;
        this.toCourse = toCourse;
        this.status = status;
    }

    public String getShiftingId() {
        return shiftingId;
    }

    public void setShiftingId(String shiftingId) {
        this.shiftingId = shiftingId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getFromCourse() {
        return fromCourse;
    }

    public void setFromCourse(String fromCourse) {
        this.fromCourse = fromCourse;
    }

    public String getToCourse() {
        return toCourse;
    }

    public void setToCourse(String toCourse) {
        this.toCourse = toCourse;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}