package model;

public class Student {
    private String studentId;
    private String name;
    private int yearLevel;     // Bag-o
    private String birthday;   // Bag-o
    private String course;     // Bag-o

    // Updated Constructor
    public Student(String studentId, String name, int yearLevel, String birthday, String course) {
        this.studentId = studentId;
        this.name = name;
        this.yearLevel = yearLevel;
        this.birthday = birthday;
        this.course = course;
    }

    // Getters
    public String getStudentId() { return studentId; }
    public String getName() { return name; }
    public int getYearLevel() { return yearLevel; }
    public String getBirthday() { return birthday; }
    public String getCourse() { return course; }

    // Para nindot tan-awon ig display/print
    @Override
    public String toString() {
        return "ID: " + studentId + 
               " | Name: " + name + 
               " | Year: " + yearLevel + 
               " | Birthday: " + birthday + 
               " | Course: " + course;
    }
}