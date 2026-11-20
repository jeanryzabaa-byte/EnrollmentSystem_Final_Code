package repository;

import java.io.IOException;
import java.util.ArrayList;
import model.Student;

public interface StudentRepository {
    void saveStudent(Student student) throws IOException;
    void saveAllStudents(ArrayList<Student> students) throws IOException;
    ArrayList<Student> getAllStudents() throws IOException;
    Student findStudentById(String id) throws IOException;
    boolean updateStudent(Student updatedStudent) throws IOException;
    boolean deleteStudentById(String id) throws IOException;
    boolean studentExists(String id) throws IOException;
}