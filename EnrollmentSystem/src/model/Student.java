package model;

public class Student {
    private String id;
    private String name;
    private String yearLevel; 
    private String birthday;
    private String course;

    public Student(String id, String name, String yearLevel, String birthday, String course) {
        this.id = id;
        this.name = name;
        this.yearLevel = yearLevel;
        this.birthday = birthday;
        this.course = course;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getYearLevel() { return yearLevel; } 
    public String getBirthday() { return birthday; }
    public String getCourse() { return course; }

    public void displayInfo() {
        System.out.println("ID: " + id + " | Name: " + name + " | Year: " + yearLevel + " | Birthday: " + birthday + " | Course: " + course);
    }
}