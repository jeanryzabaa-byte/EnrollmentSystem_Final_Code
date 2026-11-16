package service;

import java.io.IOException;
import java.util.ArrayList;
import model.Student;
import repository.StudentRepository;

public class EnrollmentService {
    private final StudentRepository repository;

    public EnrollmentService(StudentRepository repository) {
        this.repository = repository;
    }

    public void registerStudent(String id, String name, String yearLevel, String birthday, String course) 
            throws IOException {
        if (repository.studentExists(id)) {
            throw new IllegalArgumentException("Student ID already exists!");
        }

        Student student = new Student(id, name, yearLevel, birthday, course);
        repository.saveStudent(student);
    }

    public ArrayList<Student> viewStudents() throws IOException {
        return repository.getAllStudents();
    }
}