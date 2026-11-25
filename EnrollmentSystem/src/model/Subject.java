package model;

public class Subject {
    private String subjectCode;
    private String subjectTitle;
    private int units;

    public Subject() {
    }

    public Subject(String subjectCode, String subjectTitle, int units) {
        this.subjectCode = subjectCode;
        this.subjectTitle = subjectTitle;
        this.units = units;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getSubjectTitle() {
        return subjectTitle;
    }

    public void setSubjectTitle(String subjectTitle) {
        this.subjectTitle = subjectTitle;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }
}