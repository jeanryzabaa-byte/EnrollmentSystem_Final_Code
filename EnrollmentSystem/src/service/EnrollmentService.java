package service;

import java.io.IOException;
import java.util.ArrayList;
import model.Student;
import repository.StudentRepository;

public class EnrollmentService {
    private StudentRepository repository;

    public EnrollmentService(StudentRepository repository) {
        this.repository = repository;
    }

    public void registerStudent(String id, String lastName, String firstName, String middleName,
                                String yearLevel, String birthday, String course,
                                int age, String gender, String religion, String homeAddress,
                                String citizenship, String schoolYear, String placeOfBirth,
                                boolean transferee, String lastSchoolName, String lastSchoolAddress) throws IOException {
        if (repository.studentExists(id)) {
            throw new IllegalArgumentException("Student ID already exists!");
        }

        Student student = new Student(
                id, lastName, firstName, middleName,
                yearLevel, birthday, course, age,
                gender, religion, homeAddress,
                citizenship, schoolYear, placeOfBirth,
                transferee, lastSchoolName, lastSchoolAddress
        );
        repository.saveStudent(student);
    }

    public ArrayList<Student> getAllStudents() throws IOException {
        return repository.getAllStudents();
    }

    public Student searchStudentById(String id) throws IOException {
        return repository.findStudentById(id);
    }

    public void updateStudent(String id, String lastName, String firstName, String middleName, String yearLevel, String birthday, String course,
    int age, String gender, String religion, String homeAddress,
    String citizenship, String schoolYear, String placeOfBirth,
    boolean transferee, String lastSchoolName, String lastSchoolAddress) throws IOException {

        if (!repository.studentExists(id)) {
            throw new IllegalArgumentException("Student ID not found!");
        }

        Student updatedStudent = new Student(
                id, lastName, firstName, middleName,
                yearLevel, birthday, course, age,
                gender, religion, homeAddress,
                citizenship, schoolYear, placeOfBirth,
                transferee, lastSchoolName, lastSchoolAddress
        );

        boolean updated = repository.updateStudent(updatedStudent);
        if (!updated) {
            throw new IllegalArgumentException("Failed to update student record!");
        }
    }

    public boolean deleteStudent(String id) throws IOException {
        return repository.deleteStudentById(id);
    }
}