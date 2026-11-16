package repository;

import java.io.*;
import java.util.ArrayList;
import model.Student;

public class FileStudentRepository implements StudentRepository {
    
    private String filePath = "data/students.txt";

    @Override
    public ArrayList<Student> getAllStudents() throws IOException {
        ArrayList<Student> students = new ArrayList<>();
        File file = new File(filePath);
        
        if (!file.exists()) {
            return students;  }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\\|", -1);
        
                if (data.length >= 5) {
                    String id = data[0];
                    String name = data[1];
                    int yearLevel = Integer.parseInt(data[2]);
                    String birthday = data[3];
                    String course = data[4];
                    
                    students.add(new Student(id, name, yearLevel, birthday, course));
                }
            }
        }
        return students;
    }

    @Override
    public void saveStudent(Student student) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, true))) {
            writer.println(student.getId() + "|" + student.getName() + "|" + student.getYearLevel() + "|" + student.getBirthday() + "|" + student.getCourse());
        }
    }

    @Override
    public boolean studentExists(String id) throws IOException {
        for (Student student : getAllStudents()) {
            if (student.getId().equalsIgnoreCase(id)) {
                return true;
            }
        }
        return false;
    }
}