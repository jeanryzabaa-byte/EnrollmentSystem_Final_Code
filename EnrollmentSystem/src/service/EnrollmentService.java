package service;

import model.EnrollmentRecord;
import model.Payment;
import model.ShiftingRequest;
import model.Student;
import model.Subject;
import model.User;
import repository.FileRepository;
import repository.TextFileRepository;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class EnrollmentService {
    private static final Path DATA_DIR = Paths.get("data");

    private final FileRepository<User> userRepo;
    private final FileRepository<Student> studentRepo;
    private final FileRepository<Subject> subjectRepo;
    private final FileRepository<EnrollmentRecord> enrollmentRepo;
    private final FileRepository<Payment> paymentRepo;
    private final FileRepository<ShiftingRequest> shiftingRepo;

    private final List<User> users;
    private final List<Student> students;
    private final List<Subject> subjects;
    private final List<EnrollmentRecord> enrollments;
    private final List<Payment> payments;
    private final List<ShiftingRequest> shiftingRequests;

    public EnrollmentService() {
        this.userRepo = new TextFileRepository<>(DATA_DIR.resolve("users.txt"), User::toDataString, User::fromDataString);
        this.studentRepo = new TextFileRepository<>(DATA_DIR.resolve("students.txt"), Student::toDataString, Student::fromDataString);
        this.subjectRepo = new TextFileRepository<>(DATA_DIR.resolve("subjects.txt"), Subject::toDataString, Subject::fromDataString);
        this.enrollmentRepo = new TextFileRepository<>(DATA_DIR.resolve("enrollments.txt"), EnrollmentRecord::toDataString, EnrollmentRecord::fromDataString);
        this.paymentRepo = new TextFileRepository<>(DATA_DIR.resolve("payments.txt"), Payment::toDataString, Payment::fromDataString);
        this.shiftingRepo = new TextFileRepository<>(DATA_DIR.resolve("shifting_requests.txt"), ShiftingRequest::toDataString, ShiftingRequest::fromDataString);

        this.users = new ArrayList<>(userRepo.findAll());
        this.students = new ArrayList<>(studentRepo.findAll());
        this.subjects = new ArrayList<>(subjectRepo.findAll());
        this.enrollments = new ArrayList<>(enrollmentRepo.findAll());
        this.payments = new ArrayList<>(paymentRepo.findAll());
        this.shiftingRequests = new ArrayList<>(shiftingRepo.findAll());

        seedDefaultsIfNeeded();
    }

    private void seedDefaultsIfNeeded() {
        if (users.isEmpty()) {
            users.add(new User("admin", "admin123", "ADMIN", "ADMIN-01"));
            users.add(new User("registrar", "reg123", "REGISTRAR", "REG-01"));
            users.add(new User("cashier", "cash123", "CASHIER", "CASH-01"));
            users.add(new User("student", "stud123", "STUDENT", "2026-0001"));
            users.add(new User("instructor", "inst123", "INSTRUCTOR", "instructor"));
            userRepo.saveAll(users);
        }
        if (subjects.isEmpty()) {
            subjects.add(new Subject("CS101", "Introduction to Computing", 3, 500.0, "instructor"));
            subjects.add(new Subject("MATH101", "College Algebra", 3, 500.0, "instructor"));
            subjects.add(new Subject("ENG101", "Purposive Communication", 3, 450.0, ""));
            subjectRepo.saveAll(subjects);
        }
    }

    // --- Authentication & Users ---
    public User login(String username, String password) {
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username.trim()) && u.authenticate(password)) {
                return u;
            }
        }
        return null;
    }

    public boolean addUser(String username, String password, String role, String linkedId) {
        String normalizedRole = role.trim().toUpperCase();
        if (!Set.of("ADMIN", "REGISTRAR", "CASHIER", "STUDENT", "INSTRUCTOR").contains(normalizedRole)) {
            return false;
        }
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username.trim())) {
                return false;
            }
        }
        users.add(new User(username.trim(), password, normalizedRole, linkedId.trim()));
        userRepo.saveAll(users);
        return true;
    }

    public List<User> getUsers() {
        return Collections.unmodifiableList(users);
    }

    // --- Students ---
    public boolean addStudent(String studentNumber, String fullName, String course, int yearLevel) {
        if (findStudent(studentNumber) != null) return false;
        students.add(new Student(studentNumber.trim(), fullName.trim(), course.trim().toUpperCase(), yearLevel, "ACTIVE"));
        studentRepo.saveAll(students);
        return true;
    }

    public Student findStudent(String studentNumber) {
        if (studentNumber == null) return null;
        for (Student s : students) {
            if (s.getStudentNumber().equalsIgnoreCase(studentNumber.trim())) {
                return s;
            }
        }
        return null;
    }

    public List<Student> getStudents() {
        return Collections.unmodifiableList(students);
    }

    // --- Subjects ---
    public boolean addSubject(String code, String title, int units, double feePerUnit, String instructorUsername) {
        if (findSubject(code) != null) return false;
        subjects.add(new Subject(code.trim().toUpperCase(), title.trim(), units, feePerUnit, instructorUsername.trim()));
        subjectRepo.saveAll(subjects);
        return true;
    }

    public Subject findSubject(String code) {
        if (code == null) return null;
        for (Subject s : subjects) {
            if (s.getSubjectCode().equalsIgnoreCase(code.trim())) {
                return s;
            }
        }
        return null;
    }

    public List<Subject> getSubjects() {
        return Collections.unmodifiableList(subjects);
    }

    public List<Subject> getSubjectsForInstructor(String instructorUsername) {
        List<Subject> res = new ArrayList<>();
        for (Subject s : subjects) {
            if (s.getInstructorUsername().equalsIgnoreCase(instructorUsername.trim())) {
                res.add(s);
            }
        }
        return res;
    }

    // --- Enrollment & Grades ---
    public String enrollStudent(String studentNumber, String subjectCode, String semester) {
        Student st = findStudent(studentNumber);
        if (st == null) return "Student record not found for ID: " + studentNumber;

        Subject sub = findSubject(subjectCode);
        if (sub == null) return "Subject not found: " + subjectCode;

        for (EnrollmentRecord er : enrollments) {
            if (er.getStudentNumber().equalsIgnoreCase(studentNumber.trim())
                    && er.getSubjectCode().equalsIgnoreCase(subjectCode.trim())
                    && er.getSemester().equalsIgnoreCase(semester.trim())
                    && !"DROPPED".equalsIgnoreCase(er.getStatus())) {
                return "Student is already enrolled in " + sub.getSubjectCode() + " for " + semester;
            }
        }

        String id = "ENR-" + (enrollments.size() + 1001);
        enrollments.add(new EnrollmentRecord(id, st.getStudentNumber(), sub.getSubjectCode(), semester.trim(), "ENROLLED", "Pending"));
        enrollmentRepo.saveAll(enrollments);
        return "SUCCESS: Enrolled " + st.getFullName() + " in " + sub.getSubjectCode() + " (" + id + ")";
    }

    public List<EnrollmentRecord> getEnrollmentsByStudent(String studentNumber) {
        List<EnrollmentRecord> res = new ArrayList<>();
        for (EnrollmentRecord er : enrollments) {
            if (er.getStudentNumber().equalsIgnoreCase(studentNumber.trim())) {
                res.add(er);
            }
        }
        return res;
    }

    public List<EnrollmentRecord> getEnrollmentsBySubject(String subjectCode) {
        List<EnrollmentRecord> res = new ArrayList<>();
        for (EnrollmentRecord er : enrollments) {
            if (er.getSubjectCode().equalsIgnoreCase(subjectCode.trim())
                    && !"DROPPED".equalsIgnoreCase(er.getStatus())) {
                res.add(er);
            }
        }
        return res;
    }

    public boolean updateGrade(String recordId, String grade) {
        for (EnrollmentRecord er : enrollments) {
            if (er.getRecordId().equalsIgnoreCase(recordId.trim())) {
                er.setGrade(grade.trim());
                er.setStatus("COMPLETED");
                enrollmentRepo.saveAll(enrollments);
                return true;
            }
        }
        return false;
    }

    // --- Tuition & Payments ---
    public double calculateTotalAssessment(String studentNumber) {
        double total = 0.0;
        for (EnrollmentRecord er : enrollments) {
            if (er.getStudentNumber().equalsIgnoreCase(studentNumber.trim())
                    && !"DROPPED".equalsIgnoreCase(er.getStatus())) {
                Subject sub = findSubject(er.getSubjectCode());
                if (sub != null) {
                    total += sub.getTotalTuition();
                }
            }
        }
        return total;
    }

    public double calculateTotalPaid(String studentNumber) {
        double paid = 0.0;
        for (Payment p : payments) {
            if (p.getStudentNumber().equalsIgnoreCase(studentNumber.trim())) {
                paid += p.getAmount();
            }
        }
        return paid;
    }

    public double calculateBalance(String studentNumber) {
        return Math.max(0.0, calculateTotalAssessment(studentNumber) - calculateTotalPaid(studentNumber));
    }

    public String recordPayment(String studentNumber, double amount, String method) {
        Student st = findStudent(studentNumber);
        if (st == null) return "Student not found: " + studentNumber;
        if (amount <= 0) return "Amount must be greater than zero.";

        String id = "PAY-" + (payments.size() + 1001);
        payments.add(new Payment(id, st.getStudentNumber(), amount, method.trim().toUpperCase(), null));
        paymentRepo.saveAll(payments);
        return "SUCCESS: Payment recorded (" + id + ") for " + st.getStudentNumber() + " - Amount: " + String.format("%.2f", amount);
    }

    public List<Payment> getPaymentsByStudent(String studentNumber) {
        List<Payment> res = new ArrayList<>();
        for (Payment p : payments) {
            if (p.getStudentNumber().equalsIgnoreCase(studentNumber.trim())) {
                res.add(p);
            }
        }
        return res;
    }

    // --- Shifting Requests ---
    public String submitShiftingRequest(String studentNumber, String targetCourse, String reason) {
        Student st = findStudent(studentNumber);
        if (st == null) return "Student record not found for " + studentNumber;

        String id = "SHF-" + (shiftingRequests.size() + 1001);
        shiftingRequests.add(new ShiftingRequest(id, st.getStudentNumber(), st.getCourse(), targetCourse.trim().toUpperCase(), reason.trim(), "PENDING"));
        shiftingRepo.saveAll(shiftingRequests);
        return "SUCCESS: Shifting request submitted (" + id + ")";
    }

    public List<ShiftingRequest> getShiftingRequests() {
        return Collections.unmodifiableList(shiftingRequests);
    }

    public List<ShiftingRequest> getShiftingRequestsByStudent(String studentNumber) {
        List<ShiftingRequest> res = new ArrayList<>();
        for (ShiftingRequest sr : shiftingRequests) {
            if (sr.getStudentNumber().equalsIgnoreCase(studentNumber.trim())) {
                res.add(sr);
            }
        }
        return res;
    }

    public boolean processShiftingRequest(String requestId, boolean approve) {
        for (ShiftingRequest sr : shiftingRequests) {
            if (sr.getRequestId().equalsIgnoreCase(requestId.trim())) {
                sr.setStatus(approve ? "APPROVED" : "REJECTED");
                if (approve) {
                    Student st = findStudent(sr.getStudentNumber());
                    if (st != null) {
                        st.setCourse(sr.getTargetCourse());
                        st.setStatus("SHIFTED");
                        studentRepo.saveAll(students);
                    }
                }
                shiftingRepo.saveAll(shiftingRequests);
                return true;
            }
        }
        return false;
    }
}