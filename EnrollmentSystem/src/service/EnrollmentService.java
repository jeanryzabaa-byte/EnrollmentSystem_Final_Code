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

    public EnrollmentService() {
        this.studentRepository = null;
    }

    public EnrollmentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public void registerStudentPending(Student student) {
        if (student != null) {
            student.setEnrollmentStatus("PENDING");
            addOrUpdateStudent(student);
        }
    }

    public void enrollStudent(Student student, List<Subject> selectedSubjects) {
        if (student != null) {
            if (selectedSubjects != null && !selectedSubjects.isEmpty()) {
                student.setEnrolledSubjects(selectedSubjects);
                student.setEnrollmentStatus("ENROLLED");
            } else if (student.getEnrollmentStatus() == null || student.getEnrollmentStatus().isBlank()) {
                student.setEnrollmentStatus("NOT ENROLLED");
            }
            addOrUpdateStudent(student);
        }
    }

    private void addOrUpdateStudent(Student student) {
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getId() != null && students.get(i).getId().equalsIgnoreCase(student.getId())) {
                students.set(i, student);
                return;
            }
        }
        students.add(student);
    }

    public List<Student> getStudents() {
        return students;
    }

    public List<Student> getAllStudentsWithStatus() {
        return new ArrayList<>(students);
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