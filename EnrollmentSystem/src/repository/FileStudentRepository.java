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
                    String id = cleanField(data[0]);
                    if (id.isEmpty() || id.equalsIgnoreCase("null")) {
                        continue;
                    }

                    String lastName = cleanField(data[1]);
                    String firstName = cleanField(data[2]);
                    String middleName = cleanField(data[3]);
                    String yearLevel = cleanField(data[4]);
                    String birthday = cleanField(data[5]);
                    String course = cleanField(data[6]);
                    int age = parseIntSafe(data[7]);
                    String gender = cleanField(data[8]);
                    String religion = cleanField(data[9]);
                    String homeAddress = cleanField(data[10]);

                    String citizenship = data.length > 11 && !cleanField(data[11]).isEmpty()
                            ? cleanField(data[11])
                            : "Filipino";
                    String schoolYear = data.length > 12 ? cleanField(data[12]) : "";
                    String placeOfBirth = data.length > 13 ? cleanField(data[13]) : "";
                    boolean transferee = data.length > 14 && Boolean.parseBoolean(cleanField(data[14]));
                    String lastSchoolName = data.length > 15 ? cleanField(data[15]) : "";
                    String lastSchoolAddress = data.length > 16 ? cleanField(data[16]) : "";

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

        return students;
    }

    @Override
    public void saveStudent(Student student) throws IOException {
        File file = new File(filePath);
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        String record = formatStudentRecord(student);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(record);
            writer.newLine();
        }
    }

    @Override
    public void saveAllStudents(ArrayList<Student> students) throws IOException {
        File file = new File(filePath);
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
            for (Student student : students) {
                writer.write(formatStudentRecord(student));
                writer.newLine();
            }
        }
    }

    @Override
    public Student findStudentById(String id) throws IOException {
        for (Student s : getAllStudents()) {
            if (s.getId() != null && s.getId().equalsIgnoreCase(id.trim())) {
                return s;
            }
        }
        return null;
    }

    @Override
    public boolean updateStudent(Student updatedStudent) throws IOException {
        ArrayList<Student> students = getAllStudents();
        for (int i = 0; i < students.size(); i++) {
            String currentId = students.get(i).getId();
            if (currentId != null && updatedStudent.getId() != null
                    && currentId.equalsIgnoreCase(updatedStudent.getId().trim())) {
                students.set(i, updatedStudent);
                saveAllStudents(students);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteStudentById(String id) throws IOException {
        ArrayList<Student> students = getAllStudents();
        boolean removed = students.removeIf(s -> s.getId() != null && s.getId().equalsIgnoreCase(id.trim()));
        if (removed) {
            saveAllStudents(students);
        }
        return removed;
    }

    @Override
    public boolean studentExists(String id) throws IOException {
        for (Student s : getAllStudents()) {
            if (s.getId() != null && s.getId().equalsIgnoreCase(id.trim())) {
                return true;
            }
        }
        return false;
    }

    private String cleanField(String value) {
        if (value == null) {
            return "";
        }
        String trimmed = value.trim();
        return trimmed.equalsIgnoreCase("null") ? "" : trimmed;
    }

    private int parseIntSafe(String value) {
        try {
            return Integer.parseInt(cleanField(value));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String formatStudentRecord(Student student) {
        return String.join("|",
            cleanField(student.getId()),
            cleanField(student.getLastName()),
            cleanField(student.getFirstName()),
            cleanField(student.getMiddleName()),
            cleanField(student.getYearLevel()),
            cleanField(student.getBirthday()),
            cleanField(student.getCourse()),
            String.valueOf(student.getAge()),
            cleanField(student.getGender()),
            cleanField(student.getReligion()),
            cleanField(student.getHomeAddress()),
            cleanField(student.getCitizenship()),
            cleanField(student.getSchoolYear()),
            cleanField(student.getPlaceOfBirth()),
            String.valueOf(student.isTransferee()),
            cleanField(student.getLastSchoolName()),
            cleanField(student.getLastSchoolAddress())
        );
    }
}