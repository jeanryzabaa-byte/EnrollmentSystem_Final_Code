package model;

public class Subject {
    private String subjectCode;
    private String subjectTitle;
    private int units;
    private boolean hasLab;
    private double labFee;
    private double miscFee;

    public Subject() {
    }

    public Subject(String subjectCode, String subjectTitle, int units) {
        this.subjectCode = subjectCode;
        this.subjectTitle = subjectTitle;
        this.units = units;
        this.hasLab = subjectCode != null && subjectCode.trim().toUpperCase().endsWith("L");
        this.labFee = this.hasLab ? 1000.00 : 0.0;
        this.miscFee = 0.0;
    }

    public Subject(String subjectCode, String subjectTitle, int units, boolean hasLab, double labFee, double miscFee) {
        this.subjectCode = subjectCode;
        this.subjectTitle = subjectTitle;
        this.units = units;
        this.hasLab = hasLab;
        this.labFee = labFee;
        this.miscFee = miscFee;
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

    public boolean isHasLab() {
        return hasLab;
    }

    public void setHasLab(boolean hasLab) {
        this.hasLab = hasLab;
    }

    public double getLabFee() {
        return labFee;
    }

    public void setLabFee(double labFee) {
        this.labFee = labFee;
    }

    public double getMiscFee() {
        return miscFee;
    }

    public void setMiscFee(double miscFee) {
        this.miscFee = miscFee;
    }
}