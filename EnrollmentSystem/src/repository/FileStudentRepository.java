package repository;

import model.Student;
import java.io.*;
import java.util.ArrayList;

public class FileStudentRepository implements StudentRepository {
    private final String fileName = "data/students.txt";

    public FileStudentRepository() throws IOException {
        File file = new File(fileName);
        File folder = file.getParentFile();

        if (folder != null) {
            folder.mkdirs();
        }

        if (!file.exists()) {
            file.createNewFile();
        }
    }

    @Override
    public void saveStudent(Student student) throws IOException {
        try (BufferedWriter writer =
                     new BufferedWriter(new FileWriter(fileName, true))) {
            writer.write(student.getId() + "|" + student.getName()
                    + "|" + student.getCourse());
            writer.newLine();
        }
    }

    @Override
    public ArrayList<Student> getAllStudents() throws IOException {
        ArrayList<Student> students = new ArrayList<>();

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(fileName))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\\|", -1);

                if (data.length == 3) {
                    students.add(new Student(data[1], data[0], data[2]));
                }
            }
        }

        return students;
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
