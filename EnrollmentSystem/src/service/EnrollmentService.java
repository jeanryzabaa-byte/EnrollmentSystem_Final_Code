package service;

import java.util.ArrayList;
import java.util.List;
import model.Payment;
import model.ShiftingRequest;
import model.Student;
import model.Subject;
import repository.StudentRepository;

public class EnrollmentService {
    private final StudentRepository studentRepository;
    private final List<Student> students = new ArrayList<>();
    private final List<Subject> subjects = new ArrayList<>();
    private final List<Payment> payments = new ArrayList<>();
    private final List<ShiftingRequest> shiftingRequests = new ArrayList<>();
    private final List<String> adminSyncedRecords = new ArrayList<>();

    public EnrollmentService() {
        this.studentRepository = null;
    }

    public EnrollmentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public void addStudent(Student student) {
        if (student == null) {
            return;
        }
        if (student.getEnrollmentStatus() == null || student.getEnrollmentStatus().isBlank()) {
            student.setEnrollmentStatus(
                (student.getEnrolledSubjects() != null && !student.getEnrolledSubjects().isEmpty())
                    ? "ENROLLED"
                    : "NOT ENROLLED"
            );
        }
        addOrUpdateStudent(student);
    }

    public void markStudentPending(String studentId) {
        Student s = findStudentByIdOrNumber(studentId);
        if (s != null) {
            s.setEnrollmentStatus("PENDING");
            syncStudentToAdminAndPortal(s.getId());
        }
    }

    public void markStudentEnrolled(String studentId, List<Subject> selectedSubjects) {
        Student s = findStudentByIdOrNumber(studentId);
        if (s != null) {
            if (selectedSubjects != null && !selectedSubjects.isEmpty()) {
                s.setEnrolledSubjects(selectedSubjects);
            }
            s.setEnrollmentStatus("ENROLLED");
            syncStudentToAdminAndPortal(s.getId());
        }
    }

    private void addOrUpdateStudent(Student student) {
        String key = student.getStudentNumber() != null ? student.getStudentNumber() : student.getId();
        for (int i = 0; i < students.size(); i++) {
            Student existing = students.get(i);
            String existingKey = existing.getStudentNumber() != null ? existing.getStudentNumber() : existing.getId();
            if (key != null && existingKey != null && key.equalsIgnoreCase(existingKey)) {
                students.set(i, student);
                return;
            }
        }
        students.add(student);
    }

    public Student findStudentByIdOrNumber(String idOrNumber) {
        if (idOrNumber == null || idOrNumber.isBlank()) {
            return null;
        }
        for (Student s : students) {
            if ((s.getId() != null && s.getId().equalsIgnoreCase(idOrNumber)) ||
                (s.getStudentNumber() != null && s.getStudentNumber().equalsIgnoreCase(idOrNumber))) {
                return s;
            }
        }
        return null;
    }

    public List<Student> getStudents() {
        return students;
    }

    public List<Student> getAllStudentsWithStatus() {
        for (Student s : students) {
            if (s.getEnrollmentStatus() == null || s.getEnrollmentStatus().isBlank()) {
                s.setEnrollmentStatus("NOT ENROLLED");
            }
        }
        return new ArrayList<>(students);
    }

    public List<String> getAdminSyncedRecords() {
        return adminSyncedRecords;
    }

    public void syncStudentToAdminAndPortal(String studentId) {
        Student s = findStudentByIdOrNumber(studentId);
        if (s != null) {
            String record = "ID: " + s.getStudentNumber()
                    + " | Name: " + s.getFullName()
                    + " | Course: " + s.getCourse()
                    + " | Status: " + s.getEnrollmentStatus();
            adminSyncedRecords.add(record);
        }
    }

    public Student resolveStudentForPortal(String studentId) {
        return findStudentByIdOrNumber(studentId);
    }

    public void addSubject(Subject subject) {
        if (subject != null) {
            subjects.add(subject);
        }
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void addPayment(Payment payment) {
        if (payment != null) {
            payments.add(payment);
        }
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void addShiftingRequest(ShiftingRequest request) {
        if (request != null) {
            shiftingRequests.add(request);
        }
    }

    public List<ShiftingRequest> getShiftingRequests() {
        return shiftingRequests;
    }
}