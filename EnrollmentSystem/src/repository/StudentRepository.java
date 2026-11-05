package repository;

import model.Student;
import java.io.IOException;
import java.util.ArrayList;

public interface StudentRepository {
    void saveStudent(Student student) throws IOException;
    ArrayList<Student> getAllStudents() throws IOException;
    boolean studentExists(String id) throws IOException;
}
