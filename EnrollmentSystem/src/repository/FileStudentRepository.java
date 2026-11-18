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
            return students;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\\|", -1);
                
                if (data.length >= 7) {
                    String id = data[0];
                    String lastName = data[1];
                    String firstName = data[2];
                    String middleName = data[3];
                    String yearLevel = data[4];
                    String birthday = data[5];
                    String course = data[6];
                    
                    students.add(new Student(id, lastName, firstName, middleName, yearLevel, birthday, course));
                }
            }
        }
        return students;
    }

    @Override
    public void saveStudent(Student student) throws IOException {
        File dir = new File("data");
        if (!dir.exists()) {
            dir.mkdir();
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, true))) {
            writer.println(student.getId() + "|" + student.getLastName() + "|" + student.getFirstName() + "|" + 
                           student.getMiddleName() + "|" + 
                           student.getYearLevel() + "|" + 
                           student.getBirthday() + "|" + 
                           student.getCourse());
        }
    }

    @Override
    public boolean studentExists(String id) throws IOException {
        ArrayList<Student> students = getAllStudents();
        for (Student s : students) {
            if (s.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }
}