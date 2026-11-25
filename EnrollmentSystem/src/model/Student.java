package model;

public class Student {
    public String studentNo;
    public String name;
    public String course;
    public String major;
    public String yearSection;
    public String status;
    public String sex;
    public String birthDate;
    private boolean registrarApproved;
    private double waiverPercent;

    public Student() {
    }

    public Student(String studentNo, String name, String course, String major,
                   String yearSection, String status, String sex, String birthDate) {
        this.studentNo = studentNo;
        this.name = name;
        this.course = course;
        this.major = major;
        this.yearSection = yearSection;
        this.status = status;
        this.sex = sex;
        this.birthDate = birthDate;
        this.registrarApproved = false;
        this.waiverPercent = 0.0;
    }

    public boolean isRegistrarApproved() {
        return registrarApproved;
    }

    public void setRegistrarApproved(boolean registrarApproved) {
        this.registrarApproved = registrarApproved;
    }

    public double getWaiverPercent() {
        return waiverPercent;
    }

    public void setWaiverPercent(double waiverPercent) {
        if (waiverPercent < 0) {
            this.waiverPercent = 0;
        } else if (waiverPercent > 100) {
            this.waiverPercent = 100;
        } else {
            this.waiverPercent = waiverPercent;
        }
    }
}