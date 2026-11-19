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

    public Student(String id, String lastName, String firstName, String middleName, String yearLevel, String birthday, String course) {
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.yearLevel = yearLevel;
        this.birthday = birthday;
        this.course = course;
    }

    public String getId() { return id; }
    public String getLastName() { return lastName; }
    public String getFirstName() { return firstName; }
    public String getMiddleName() { return middleName; }
    public String getYearLevel() { return yearLevel; }
    public String getBirthday() { return birthday; }
    public String getCourse() { return course; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getReligion() { return religion; }
    public void setReligion(String religion) { this.religion = religion; }

    public String getHomeAddress() { return homeAddress; }
    public void setHomeAddress(String homeAddress) { this.homeAddress = homeAddress; }

    public void displayInfo() {
        System.out.println("ID: " + id + " | Name: " + lastName + ", " + firstName + " " + middleName + 
        " | Year: " + yearLevel + " | Birthday: " + birthday + " | Course: " + course);
    }
        public Student(String id, String lastName, String firstName, String middleName, String yearLevel, String birthday, String course, int age, String gender, String religion, String homeAddress) {

    this.age = age;
    this.gender = gender;
    this.religion = religion;
    this.homeAddress = homeAddress;

    }
}