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
import java.time.LocalDate;
import java.util.*;

public class EnrollmentService {
    private static final double DEFAULT_LAB_FEE = 1000.0;
    private static final double DEFAULT_MISC_FEE = 1500.0;

    private final FileRepository<User> userRepo;
    private final FileRepository<Student> studentRepo;
    private final FileRepository<Subject> subjectRepo;
    private final FileRepository<EnrollmentRecord> enrollmentRepo;
    private final FileRepository<Payment> paymentRepo;
    private final FileRepository<ShiftingRequest> shiftingRepo;

    private final Map<String, Double> labFees = new HashMap<>();
    private final Map<String, Double> miscFees = new HashMap<>();
    private final Map<String, Double> waivedAmounts = new HashMap<>();

    public EnrollmentService() {
        Path dataDir = resolveDataDir();
        this.userRepo = new TextFileRepository<>(dataDir.resolve("users.txt"), User::toDataString, User::fromDataString);
        this.studentRepo = new TextFileRepository<>(dataDir.resolve("students.txt"), Student::toDataString, Student::fromDataString);
        this.subjectRepo = new TextFileRepository<>(dataDir.resolve("subjects.txt"), Subject::toDataString, Subject::fromDataString);
        this.enrollmentRepo = new TextFileRepository<>(dataDir.resolve("enrollments.txt"), EnrollmentRecord::toDataString, EnrollmentRecord::fromDataString);
        this.paymentRepo = new TextFileRepository<>(dataDir.resolve("payments.txt"), Payment::toDataString, Payment::fromDataString);
        this.shiftingRepo = new TextFileRepository<>(dataDir.resolve("shifting_requests.txt"), ShiftingRequest::toDataString, ShiftingRequest::fromDataString);
        ensureFilesInitialized();
    }

    private Path resolveDataDir() {
        Path current = Paths.get("data");
        if (current.toFile().exists()) return current;
        Path nested = Paths.get("EnrollmentSystem", "data");
        if (nested.toFile().exists()) return nested;
        return current;
    }

    private void ensureFilesInitialized() {
        if (userRepo.findAll().isEmpty()) {
            userRepo.saveAll(new ArrayList<>());
        }
        if (subjectRepo.findAll().isEmpty()) {
            subjectRepo.saveAll(new ArrayList<>());
        }
        if (studentRepo.findAll().isEmpty()) {
            studentRepo.saveAll(new ArrayList<>());
        }
    }

    public User login(String username, String password) {
        if (username == null || password == null) return null;
        for (User u : userRepo.findAll()) {
            if (u.getUsername().equalsIgnoreCase(username.trim()) && u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }

    public boolean addUser(String username, String password, String role, String linkedId) {
        String normalizedRole = role == null ? "" : role.trim().toUpperCase();
        if (normalizedRole.equals("SYSTEM ADMINISTRATOR")) normalizedRole = "ADMIN";
        if (!Set.of("ADMIN", "REGISTRAR", "CASHIER", "STUDENT", "INSTRUCTOR").contains(normalizedRole)) {
            return false;
        }
        List<User> users = userRepo.findAll();
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username.trim())) return false;
        }
        users.add(new User(username.trim(), password, normalizedRole, linkedId.trim()));
        userRepo.saveAll(users);
        return true;
    }

    public List<User> getUsers() { return userRepo.findAll(); }

    public boolean addStudent(String studentNumber, String fullName, String course, int yearLevel) {
        return addStudentWithType(studentNumber, fullName, course, yearLevel, "REGULAR");
    }

    public boolean addStudentWithType(String studentNumber, String fullName, String course, int yearLevel, String studentType) {
        String sn = studentNumber.trim();
        List<Student> students = studentRepo.findAll();
        for (Student s : students) {
            if (s.getStudentNumber().equalsIgnoreCase(sn)) return false;
        }
        String type = (studentType == null || studentType.isBlank()) ? "REGULAR" : studentType.trim().toUpperCase();
        students.add(new Student(sn, fullName.trim(), course.trim().toUpperCase(), yearLevel, type, "ENROLLED"));
        studentRepo.saveAll(students);
        labFees.putIfAbsent(sn, DEFAULT_LAB_FEE);
        miscFees.putIfAbsent(sn, DEFAULT_MISC_FEE);
        waivedAmounts.putIfAbsent(sn, 0.0);
        return true;
    }

    public Student findStudent(String studentNumber) {
        if (studentNumber == null) return null;
        for (Student s : studentRepo.findAll()) {
            if (s.getStudentNumber().equalsIgnoreCase(studentNumber.trim())) return s;
        }
        return null;
    }

    public List<Student> getStudents() { return studentRepo.findAll(); }

    public boolean addSubject(String code, String title, int units, double feePerUnit, String instructor) {
        String subjCode = code.trim().toUpperCase();
        List<Subject> subjects = subjectRepo.findAll();
        for (Subject s : subjects) {
            if (s.getCode().equalsIgnoreCase(subjCode)) return false;
        }
        String inst = (instructor == null || instructor.isBlank()) ? "Unassigned" : instructor.trim();
        subjects.add(new Subject(subjCode, title.trim(), units, feePerUnit, inst));
        subjectRepo.saveAll(subjects);
        return true;
    }

    public List<Subject> getSubjects() { return subjectRepo.findAll(); }

    public Subject findSubject(String code) {
        if (code == null) return null;
        for (Subject s : subjectRepo.findAll()) {
            if (s.getCode().equalsIgnoreCase(code.trim())) return s;
        }
        return null;
    }

    public List<Subject> getSubjectsForInstructor(String instructorId) {
        List<Subject> list = new ArrayList<>();
        for (Subject s : subjectRepo.findAll()) {
            if (s.getInstructor().equalsIgnoreCase(instructorId.trim())) list.add(s);
        }
        return list;
    }

    public String enrollStudent(String studentNumber, String subjectCode, String semester) {
        String sn = studentNumber.trim();
        String sc = subjectCode.trim().toUpperCase();
        if (findStudent(sn) == null) return "Student not found.";
        if (findSubject(sc) == null) return "Subject not found.";

        List<EnrollmentRecord> records = enrollmentRepo.findAll();
        for (EnrollmentRecord r : records) {
            if (r.getStudentNumber().equalsIgnoreCase(sn)
                    && r.getSubjectCode().equalsIgnoreCase(sc)
                    && r.getSemester().equalsIgnoreCase(semester.trim())) {
                return "Student is already enrolled in this subject for the given semester.";
            }
        }
        String id = "ENR-" + (records.size() + 1) + "-" + System.currentTimeMillis() % 10000;
        records.add(new EnrollmentRecord(id, sn, sc, semester.trim(), "ENROLLED", "N/A"));
        enrollmentRepo.saveAll(records);
        return "Enrollment successful. Record ID: " + id;
    }

    public List<EnrollmentRecord> getEnrollmentsByStudent(String studentNumber) {
        List<EnrollmentRecord> list = new ArrayList<>();
        for (EnrollmentRecord r : enrollmentRepo.findAll()) {
            if (r.getStudentNumber().equalsIgnoreCase(studentNumber.trim())) list.add(r);
        }
        return list;
    }

    public List<EnrollmentRecord> getEnrollmentsBySubject(String subjectCode) {
        List<EnrollmentRecord> list = new ArrayList<>();
        for (EnrollmentRecord r : enrollmentRepo.findAll()) {
            if (r.getSubjectCode().equalsIgnoreCase(subjectCode.trim())) list.add(r);
        }
        return list;
    }

    public boolean updateGrade(String recordId, String grade) {
        List<EnrollmentRecord> records = enrollmentRepo.findAll();
        for (EnrollmentRecord r : records) {
            if (r.getRecordId().equalsIgnoreCase(recordId.trim())) {
                r.setGrade(grade.trim().toUpperCase());
                r.setStatus("GRADED");
                enrollmentRepo.saveAll(records);
                return true;
            }
        }
        return false;
    }

    public double calculateTotalAssessment(String studentNumber) {
        double tuition = 0.0;
        Map<String, Subject> subjectMap = new HashMap<>();
        for (Subject s : subjectRepo.findAll()) {
            subjectMap.put(s.getCode().toUpperCase(), s);
        }
        for (EnrollmentRecord r : getEnrollmentsByStudent(studentNumber)) {
            Subject s = subjectMap.get(r.getSubjectCode().toUpperCase());
            if (s != null) tuition += s.getUnits() * s.getFeePerUnit();
        }
        return tuition;
    }

    public void setAdditionalFees(String studentNumber, double labFee, double miscFee) {
        String sn = studentNumber.trim();
        labFees.put(sn, Math.max(0.0, labFee));
        miscFees.put(sn, Math.max(0.0, miscFee));
    }

    public void waiveFee(String studentNumber, double amount) {
        String sn = studentNumber.trim();
        double current = waivedAmounts.getOrDefault(sn, 0.0);
        waivedAmounts.put(sn, current + Math.max(0.0, amount));
    }

    public double getLabFee(String studentNumber) {
        return labFees.getOrDefault(studentNumber.trim(), DEFAULT_LAB_FEE);
    }

    public double getMiscFee(String studentNumber) {
        return miscFees.getOrDefault(studentNumber.trim(), DEFAULT_MISC_FEE);
    }

    public double getWaivedAmount(String studentNumber) {
        return waivedAmounts.getOrDefault(studentNumber.trim(), 0.0);
    }

    public double calculateTotalPaid(String studentNumber) {
        double paid = 0.0;
        for (Payment p : getPaymentsByStudent(studentNumber)) paid += p.getAmount();
        return paid;
    }

    public double calculateBalance(String studentNumber) {
        double total = Math.max(0.0,
                calculateTotalAssessment(studentNumber) + getLabFee(studentNumber) + getMiscFee(studentNumber) - getWaivedAmount(studentNumber));
        return Math.max(0.0, total - calculateTotalPaid(studentNumber));
    }

    public String recordPayment(String studentNumber, double amount, String method) {
        String sn = studentNumber.trim();
        if (findStudent(sn) == null) return "Student not found.";
        if (amount <= 0) return "Amount must be greater than zero.";
        List<Payment> payments = paymentRepo.findAll();
        String id = "PAY-" + (payments.size() + 1) + "-" + System.currentTimeMillis() % 10000;
        payments.add(new Payment(id, sn, amount, method.trim().toUpperCase(), LocalDate.now().toString()));
        paymentRepo.saveAll(payments);
        return "Payment recorded. Receipt ID: " + id;
    }

    public List<Payment> getPaymentsByStudent(String studentNumber) {
        List<Payment> list = new ArrayList<>();
        for (Payment p : paymentRepo.findAll()) {
            if (p.getStudentNumber().equalsIgnoreCase(studentNumber.trim())) list.add(p);
        }
        return list;
    }

    public String submitShiftingRequest(String studentNumber, String targetCourse, String reason) {
        String sn = studentNumber.trim();
        Student st = findStudent(sn);
        if (st == null) return "Student record not found.";
        List<ShiftingRequest> list = shiftingRepo.findAll();
        String id = "SHF-" + (list.size() + 1) + "-" + System.currentTimeMillis() % 10000;
        list.add(new ShiftingRequest(id, sn, st.getCourse(), targetCourse.trim().toUpperCase(), reason.trim(), "PENDING"));
        shiftingRepo.saveAll(list);
        return "Shifting request submitted. Request ID: " + id;
    }

    public List<ShiftingRequest> getShiftingRequests() { return shiftingRepo.findAll(); }

    public List<ShiftingRequest> getShiftingRequestsByStudent(String studentNumber) {
        List<ShiftingRequest> list = new ArrayList<>();
        for (ShiftingRequest sr : shiftingRepo.findAll()) {
            if (sr.getStudentNumber().equalsIgnoreCase(studentNumber.trim())) list.add(sr);
        }
        return list;
    }

    public boolean processShiftingRequest(String requestId, boolean approve) {
        List<ShiftingRequest> list = shiftingRepo.findAll();
        for (ShiftingRequest sr : list) {
            if (sr.getRequestId().equalsIgnoreCase(requestId.trim())) {
                sr.setStatus(approve ? "APPROVED" : "REJECTED");
                shiftingRepo.saveAll(list);
                if (approve) {
                    List<Student> students = studentRepo.findAll();
                    for (Student s : students) {
                        if (s.getStudentNumber().equalsIgnoreCase(sr.getStudentNumber())) {
                            s.setCourse(sr.getTargetCourse());
                            studentRepo.saveAll(students);
                            break;
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }
}