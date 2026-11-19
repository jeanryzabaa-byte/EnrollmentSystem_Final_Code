package service;

import java.io.IOException;
import model.Student;
import repository.StudentRepository;

public class EnrollmentService {
    private StudentRepository repository;

    public EnrollmentService(StudentRepository repository) {
        this.repository = repository;
    }

    public void registerStudent(String id, String lastName, String firstName, String middleName,
                            String yearLevel, String birthday, String course,
                            int age, String gender, String religion, String homeAddress) throws IOException {
    if (repository.studentExists(id)) {
        throw new IllegalArgumentException("Student ID already exists!");
    }

    Student student = new Student(id, lastName, firstName, middleName, yearLevel, birthday, course,
                age, gender, religion, homeAddress);
    repository.save(student); 
}