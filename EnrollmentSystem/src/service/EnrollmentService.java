package service;

import model.Student;
import repository.StudentRepository;
import java.io.IOException;
import java.util.ArrayList;

public class EnrollmentService {
    private final StudentRepository repository;

    public EnrollmentService(StudentRepository repository) {
        this.repository = repository;
    }

    public void registerStudent(String name, String id, String course)
            throws IOException {
        if (repository.studentExists(id)) {
            throw new IllegalArgumentException("Student ID already exists!");
        }

        Student student = new Student(name, id, course);
        student.enroll();
        repository.saveStudent(student);
    }

    public ArrayList<Student> viewStudents() throws IOException {
        return repository.getAllStudents();
    }
}
