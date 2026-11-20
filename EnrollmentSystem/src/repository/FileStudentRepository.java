package repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split("\\|", -1);

                if (data.length >= 11) {
                String id = data[0].trim();
                String lastName = data[1].trim();
                String firstName = data[2].trim();
                String middleName = data[3].trim();
                String yearLevel = data[4].trim();
                String birthday = data[5].trim();
                String course = data[6].trim();
                int age = Integer.parseInt(data[7].trim());
                String gender = data[8].trim();
                String religion = data[9].trim();
                String homeAddress = data[10].trim();

                String citizenship = data.length > 11 ? data[11].trim() : "Filipino";
                String schoolYear = data.length > 12 ? data[12].trim() : "";
                String placeOfBirth = data.length > 13 ? data[13].trim() : "";
                boolean transferee = data.length > 14 && Boolean.parseBoolean(data[14].trim());
                String lastSchoolName = data.length > 15 ? data[15].trim() : "";
                String lastSchoolAddress = data.length > 16 ? data[16].trim() : "";

                Student student = new Student(
                    id, lastName, firstName, middleName,
                    yearLevel, birthday, course, age,
                    gender, religion, homeAddress,
                    citizenship, schoolYear, placeOfBirth,
                    transferee, lastSchoolName, lastSchoolAddress
                );
                students.add(student);
            }
                }
            }
        }

     String record = String.join("|",
            student.getId(),
            student.getLastName(),
            student.getFirstName(),
            student.getMiddleName(),
            student.getYearLevel(),
            student.getBirthday(),
            student.getCourse(),
            String.valueOf(student.getAge()),
            student.getGender(),
            student.getReligion(),
            student.getHomeAddress(),
            student.getCitizenship(),
            student.getSchoolYear(),
            student.getPlaceOfBirth(),
            String.valueOf(student.isTransferee()),
            student.getLastSchoolName(),
            student.getLastSchoolAddress()
        );
            student.getFirstName(),
            student.getMiddleName(),
            student.getYearLevel(),
            student.getBirthday(),
            student.getCourse(),
            String.valueOf(student.getAge()),
            student.getGender(),
            student.getReligion(),
            student.getHomeAddress()
        );

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(record);
            writer.newLine();
        }
    }

    @Override
    public boolean studentExists(String id) throws IOException {
        for (Student s : getAllStudents()) {
            if (s.getId().equalsIgnoreCase(id)) {
                return true;
            }
        }
        return false;
    }
}