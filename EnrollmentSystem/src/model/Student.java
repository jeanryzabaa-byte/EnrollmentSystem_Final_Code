package model;

public class Student extends Enrollment {
    private final String name;
    private final String id;
    private final String course;

    public Student(String name, String id, String course) {
        super(name, id);
        this.name = name;
        this.id = id;
        this.course = course;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getCourse() {
        return course;
    }

    @Override
    public void enroll() {
        System.out.println("Student successfully enrolled!");
    }

    public void displayInfo() {
        System.out.println("------------------------------");
        System.out.println("Student ID : " + id);
        System.out.println("Name       : " + name);
        System.out.println("Course     : " + course);
        System.out.println("------------------------------");
    }
}
