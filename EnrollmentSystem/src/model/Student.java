package model;

public class Student {
    private String id;
    private String lastName;
    private String firstName;
    private String middleName;
    private String yearLevel;
    private String birthday;
    private String course;

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

    public void displayInfo() {
        System.out.println("ID: " + id + " | Name: " + lastName + ", " + firstName + " " + middleName + 
        " | Year: " + yearLevel + " | Birthday: " + birthday + " | Course: " + course);
    }
}