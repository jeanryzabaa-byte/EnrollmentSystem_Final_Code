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

    private String gender;
    private String religion;
    private String homeAddress;
    private String citizenship;
    private String schoolYear;
    private String placeOfBirth;
    private boolean transferee;
    private String lastSchoolName;
    private String lastSchoolAddress;

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
        this.gender = sex;
        this.registrarApproved = false;
        this.waiverPercent = 0.0;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getYearSection() {
        return yearSection;
    }

    public void setYearSection(String yearSection) {
        this.yearSection = yearSection;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return (gender != null) ? gender : sex;
    }

    public void setGender(String gender) {
        this.gender = gender;
        if (this.sex == null) {
            this.sex = gender;
        }
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public String getCitizenship() {
        return citizenship;
    }

    public void setCitizenship(String citizenship) {
        this.citizenship = citizenship;
    }

    public String getSchoolYear() {
        return schoolYear;
    }

    public void setSchoolYear(String schoolYear) {
        this.schoolYear = schoolYear;
    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    public void setPlaceOfBirth(String placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }

    public boolean isTransferee() {
        return transferee;
    }

    public void setTransferee(boolean transferee) {
        this.transferee = transferee;
    }

    public String getLastSchoolName() {
        return lastSchoolName;
    }

    public void setLastSchoolName(String lastSchoolName) {
        this.lastSchoolName = lastSchoolName;
    }

    public String getLastSchoolAddress() {
        return lastSchoolAddress;
    }

    public void setLastSchoolAddress(String lastSchoolAddress) {
        this.lastSchoolAddress = lastSchoolAddress;
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