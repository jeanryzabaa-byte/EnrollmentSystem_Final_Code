package model;

public class Student {
    private String id;
    private String lastName;
    private String firstName;
    private String middleName;
    private String yearLevel;
    private String birthday;
    private String course;
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

    public void displayInfo() {
        System.out.println("ID              : " + id);
        System.out.println("Name            : " + lastName + ", " + firstName + " " + middleName);
        System.out.println("Year & Course   : " + yearLevel + " - " + course);
        System.out.println("Birthday / Age  : " + birthday + " (" + age + ")");
        System.out.println("Gender/Religion : " + gender + " / " + religion);
        System.out.println("Address         : " + homeAddress);
        System.out.println("Citizenship     : " + citizenship);
        System.out.println("School Year     : " + schoolYear);
        System.out.println("Place of Birth  : " + placeOfBirth);
        System.out.println("Transferee      : " + (transferee ? "Yes" : "No"));
        if (transferee) {
            System.out.println("Last School     : " + lastSchoolName + " (" + lastSchoolAddress + ")");
        }
    }

    public Student() {
    }

    public Student(String id, String lastName, String firstName, String middleName,
                   String yearLevel, String birthday, String course, int age,
                   String gender, String religion, String homeAddress) {
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.yearLevel = yearLevel;
        this.birthday = birthday;
        this.course = course;
        this.age = age;
        this.gender = gender;
        this.religion = religion;
        this.homeAddress = homeAddress;
        this.citizenship = "Filipino";
        this.schoolYear = "";
        this.placeOfBirth = "";
        this.transferee = false;
        this.lastSchoolName = "";
        this.lastSchoolAddress = "";
    }

    public Student(String id, String lastName, String firstName, String middleName, String yearLevel, String birthday, String course, int age, String gender, String religion, String homeAddress, String citizenship, String schoolYear, String placeOfBirth,
    boolean transferee, String lastSchoolName, String lastSchoolAddress) {
        
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.yearLevel = yearLevel;
        this.birthday = birthday;
        this.course = course;
        this.age = age;
        this.gender = gender;
        this.religion = religion;
        this.homeAddress = homeAddress;
        this.citizenship = citizenship;
        this.schoolYear = schoolYear;
        this.placeOfBirth = placeOfBirth;
        this.transferee = transferee;
        this.lastSchoolName = lastSchoolName;
        this.lastSchoolAddress = lastSchoolAddress;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
        return yearLevel;
    }

    public void setYearLevel(String yearLevel) {
        this.yearLevel = yearLevel;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
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
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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
}