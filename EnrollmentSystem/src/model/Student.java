package model;

public class Student {
    private String id;
    private String name;
    private int yearLevel;     // Bag-o
    private String birthday;   // Bag-o
    private String course;     // Bag-o

    // Constructor
    public Student(String id, String name, int yearLevel, String birthday, String course) {
        this.id = id;
        this.name = name;
        this.yearLevel = yearLevel;
        this.birthday = birthday;
        this.course = course;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public int getYearLevel() { return yearLevel; }
    public String getBirthday() { return birthday; }
    public String getCourse() { return course; }

}