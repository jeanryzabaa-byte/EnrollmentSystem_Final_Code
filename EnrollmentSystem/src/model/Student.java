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

    private String id;
    private String lastName;
    private String firstName;
    private String middleName;
    private String yearLevel;
    private String birthday;
    private int age;

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
        this.id = studentNo;
        this.name = name;
        this.course = course;
        this.major = major;
        this.yearSection = yearSection;
        this.yearLevel = yearSection;
        this.status = status;
        this.sex = sex;
        this.gender = sex;
        this.birthDate = birthDate;
        this.birthday = birthDate;
    }

    // 17-argument constructor para sa FileStudentRepository
    public Student(String id, String lastName, String firstName, String middleName,
                   String yearLevel, String birthday, String course, int age,
                   String gender, String religion, String homeAddress, String citizenship,
                   String schoolYear, String placeOfBirth, boolean transferee,
                   String lastSchoolName, String lastSchoolAddress) {
        this.id = id;
        this.studentNo = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.name = buildFullName(firstName, middleName, lastName);
        this.yearLevel = yearLevel;
        this.yearSection = yearLevel;
        this.birthday = birthday;
        this.birthDate = birthday;
        this.course = course;
        this.age = age;
        this.gender = gender;
        this.sex = gender;
        this.religion = religion;
        this.homeAddress = homeAddress;
        this.citizenship = citizenship;
        this.schoolYear = schoolYear;
        this.placeOfBirth = placeOfBirth;
        this.transferee = transferee;
        this.lastSchoolName = lastSchoolName;
        this.lastSchoolAddress = lastSchoolAddress;
        this.status = transferee ? "Transferee" : "Regular";
    }

    private String buildFullName(String first, String middle, String last) {
        String f = (first != null) ? first.trim() : "";
        String m = (middle != null) ? middle.trim() : "";
        String l = (last != null) ? last.trim() : "";
        String full = (f + " " + m + " " + l).replaceAll("\\s+", " ").trim();
        return full.isEmpty() ? "" : full;
    }

    public String getId() {
        return (id != null) ? id : studentNo;
    }

    public void setId(String id) {
        this.id = id;
        this.studentNo = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getYearLevel() {
        return (yearLevel != null) ? yearLevel : yearSection;
    }

    public void setYearLevel(String yearLevel) {
        this.yearLevel = yearLevel;
        this.yearSection = yearLevel;
    }

    public String getBirthday() {
        return (birthday != null) ? birthday : birthDate;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
        this.birthDate = birthday;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
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

    public String getStudentNo() {
        return (studentNo != null) ? studentNo : id;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
        this.id = studentNo;
    }

    public String getName() {
        return (name != null) ? name : buildFullName(firstName, middleName, lastName);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getYearSection() {
        return (yearSection != null) ? yearSection : yearLevel;
    }

    public void setYearSection(String yearSection) {
        this.yearSection = yearSection;
        this.yearLevel = yearSection;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSex() {
        return (sex != null) ? sex : gender;
    }

    public void setSex(String sex) {
        this.sex = sex;
        this.gender = sex;
    }

    public String getBirthDate() {
        return (birthDate != null) ? birthDate : birthday;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
        this.birthday = birthDate;
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