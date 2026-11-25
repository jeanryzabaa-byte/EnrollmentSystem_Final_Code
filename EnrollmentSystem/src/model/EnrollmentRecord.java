package model;

public class EnrollmentRecord {
    private String enrollmentId;
    private String studentId;
    private String subjectCode;
    private String semester;
    private String status;

    public EnrollmentRecord() {
    }

    public EnrollmentRecord(String enrollmentId, String studentId, String subjectCode, String semester, String status) {
        this.enrollmentId = enrollmentId;
        this.studentId = studentId;
        this.subjectCode = subjectCode;
        this.semester = semester;
        this.status = status;
    }

    public String getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(String enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}