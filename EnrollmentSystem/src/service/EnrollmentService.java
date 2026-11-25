package service;

import model.*;
import repository.*;

import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

public class EnrollmentService {
    private static final double DEFAULT_LAB_FEE = 1000.0;
    private static final double DEFAULT_MISC_FEE = 1500.0;
    private static final List<String> DEFAULT_INSTRUCTORS = List.of(
            "Frances Cambaya", "Asma Chistopher", "Danna Remojo"
    );

    private final Path dataDir;
    private final FileRepository<User> userRepo;
    private final FileRepository<Student> studentRepo;
    private final FileRepository<Subject> subjectRepo;
    private final FileRepository<EnrollmentRecord> enrollmentRepo;
    private final FileRepository<Payment> paymentRepo;
    private final FileRepository<ShiftingRequest> shiftingRepo;

    private final Map<String, Double> labFees = new HashMap<>();
    private final Map<String, Double> miscFees = new HashMap<>();
    private final Map<String, Double> waivedAmounts = new HashMap<>();
    private final List<String> pendingStudentIdsForAccount = new ArrayList<>();
    private final List<String> adminSyncedRecords = new ArrayList<>();

    public EnrollmentService() {
        this.dataDir = Files.exists(Paths.get("data")) ? Paths.get("data") : Paths.get("EnrollmentSystem", "data");
        this.userRepo = new TextFileRepository<>(dataDir.resolve("users.txt"), User::toDataString, User::fromDataString);
        this.studentRepo = new TextFileRepository<>(dataDir.resolve("students.txt"), Student::toDataString, Student::fromDataString);
        this.subjectRepo = new TextFileRepository<>(dataDir.resolve("subjects.txt"), Subject::toDataString, Subject::fromDataString);
        this.enrollmentRepo = new TextFileRepository<>(dataDir.resolve("enrollments.txt"), EnrollmentRecord::toDataString, EnrollmentRecord::fromDataString);
        this.paymentRepo = new TextFileRepository<>(dataDir.resolve("payments.txt"), Payment::toDataString, Payment::fromDataString);
        this.shiftingRepo = new TextFileRepository<>(dataDir.resolve("shifting_requests.txt"), ShiftingRequest::toDataString, ShiftingRequest::fromDataString);

        if (userRepo.findAll().isEmpty()) userRepo.saveAll(new ArrayList<>());
        if (studentRepo.findAll().isEmpty()) studentRepo.saveAll(new ArrayList<>());
        if (subjectRepo.findAll().isEmpty()) {
            subjectRepo.saveAll(List.of(
                    new Subject("CS101", "Introduction to Computing", 3, 500.0, "Frances Cambaya"),
                    new Subject("MATH101", "College Algebra", 3, 500.0, "Asma Chistopher"),
                    new Subject("ENG101", "Purposive Communication", 3, 450.0, "Danna Remojo")
            ));
        }
        assignDefaultInstructorsToUnassignedSubjects();
        refreshPendingStudentIds();
    }

    private double readFeeFromFile(String targetKey, boolean sumNonTuition, double fallback) {
        Path feesFile = dataDir.resolve("fees.txt");
        if (!Files.exists(feesFile)) return fallback;
        double total = 0.0;
        boolean found = false;
        try {
            for (String line : Files.readAllLines(feesFile)) {
                if (line == null || line.isBlank()) continue;
                String[] p = line.split("\\|");
                if (p.length < 2) continue;
                String key = p[0].trim().toUpperCase();
                double val = Double.parseDouble(p[1].trim());
                if (!sumNonTuition && key.equals(targetKey)) return val;
                if (sumNonTuition && !key.equals("TUITION_PER_UNIT")) {
                    total += val;
                    found = true;
                }
            }
        } catch (Exception ignored) { }
        return sumNonTuition ? (found ? total : fallback) : fallback;
    }

    public double loadMiscFeesFromFile() { return readFeeFromFile("", true, DEFAULT_MISC_FEE); }
    public double getTuitionRateFromFile() { return readFeeFromFile("TUITION_PER_UNIT", false, 150.0); }

    private String resolveInstructor(Subject subj, int fallbackIndex) {
        if (subj != null && subj.getInstructor() != null) {
            String inst = subj.getInstructor().trim();
            if (!inst.isEmpty() && !inst.equalsIgnoreCase("Unassigned") && !inst.equalsIgnoreCase("instructor")) {
                return inst;
            }
        }
        return DEFAULT_INSTRUCTORS.get(Math.abs(fallbackIndex) % DEFAULT_INSTRUCTORS.size());
    }

    public void assignDefaultInstructorsToUnassignedSubjects() {
        List<Subject> subjects = subjectRepo.findAll();
        boolean changed = false;
        for (int i = 0; i < subjects.size(); i++) {
            Subject s = subjects.get(i);
            String cur = s.getInstructor() == null ? "" : s.getInstructor().trim();
            if (cur.isEmpty() || cur.equalsIgnoreCase("Unassigned") || cur.equalsIgnoreCase("instructor")) {
                subjects.set(i, new Subject(s.getCode(), s.getTitle(), s.getUnits(), s.getFeePerUnit(), resolveInstructor(null, i)));
                changed = true;
            }
        }
        if (changed) subjectRepo.saveAll(subjects);
    }

    public void refreshPendingStudentIds() {
        pendingStudentIdsForAccount.clear();
        Set<String> existing = new HashSet<>();
        for (User u : userRepo.findAll()) {
            if ("STUDENT".equalsIgnoreCase(u.getRole()) && u.getLinkedId() != null) {
                existing.add(u.getLinkedId().trim().toUpperCase());
            }
        }
        for (Student s : studentRepo.findAll()) {
            String sn = s.getStudentNumber().trim();
            if (!existing.contains(sn.toUpperCase()) && !pendingStudentIdsForAccount.contains(sn)) {
                pendingStudentIdsForAccount.add(sn);
            }
        }
    }

    public List<String> getPendingStudentIdsForAccount() {
        refreshPendingStudentIds();
        return new ArrayList<>(pendingStudentIdsForAccount);
    }

    public List<String> getAdminSyncedRecords() { return new ArrayList<>(adminSyncedRecords); }

    public void syncStudentToAdminAndPortal(String studentNumber) {
        Student st = findStudent(studentNumber);
        if (st == null) return;
        st.setStatus(getEnrollmentsByStudent(st.getStudentNumber()).isEmpty() ? "REGISTERED" : "ENROLLED");
        List<Student> all = studentRepo.findAll();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getStudentNumber().equalsIgnoreCase(st.getStudentNumber())) {
                all.set(i, st);
                break;
            }
        }
        studentRepo.saveAll(all);
        String summary = st + " | Balance: " + String.format("%.2f", calculateBalance(st.getStudentNumber()));
        adminSyncedRecords.removeIf(r -> r.startsWith("[" + st.getStudentNumber() + "]"));
        adminSyncedRecords.add(summary);
    }

    public String generateNextStudentNumber() {
        int year = LocalDate.now().getYear();
        int seq = studentRepo.findAll().size() + 1;
        while (findStudent(String.format("%d-%04d", year, seq)) != null) seq++;
        return String.format("%d-%04d", year, seq);
    }

    public User login(String username, String password) {
        if (username == null || password == null) return null;
        for (User u : userRepo.findAll()) {
            if (u.getUsername().equalsIgnoreCase(username.trim()) && u.getPassword().equals(password)) return u;
        }
        return null;
    }

    public boolean addUser(String username, String password, String role, String linkedId) {
        String norm = role == null ? "" : role.trim().toUpperCase();
        if (norm.equals("SYSTEM ADMINISTRATOR")) norm = "ADMIN";
        if (!Set.of("ADMIN", "REGISTRAR", "CASHIER", "STUDENT", "INSTRUCTOR").contains(norm)) return false;
        for (User u : userRepo.findAll()) {
            if (u.getUsername().equalsIgnoreCase(username.trim())) return false;
        }
        List<User> users = userRepo.findAll();
        users.add(new User(username.trim(), password, norm, linkedId.trim()));
        userRepo.saveAll(users);
        refreshPendingStudentIds();
        return true;
    }

    public List<User> getUsers() { return userRepo.findAll(); }

    public List<User> getAddedUsersOnly() {
        Set<String> defaults = Set.of("admin", "registrar", "cashier", "student", "instructor");
        List<User> added = new ArrayList<>();
        for (User u : userRepo.findAll()) {
            if (!defaults.contains(u.getUsername().toLowerCase())) added.add(u);
        }
        return added.isEmpty() ? userRepo.findAll() : added;
    }

    public String addStudentWithType(String studentNumber, String fullName, String course, int yearLevel, String studentType) {
        String sn = (studentNumber == null || studentNumber.isBlank() || studentNumber.equalsIgnoreCase("AUTO"))
                ? generateNextStudentNumber() : studentNumber.trim();
        if (findStudent(sn) != null) return null;
        String type = (studentType == null || studentType.isBlank()) ? "REGULAR" : studentType.trim().toUpperCase();
        List<Student> students = studentRepo.findAll();
        students.add(new Student(sn, fullName.trim(), course.trim().toUpperCase(), yearLevel, type, "REGISTERED"));
        studentRepo.saveAll(students);
        labFees.putIfAbsent(sn, DEFAULT_LAB_FEE);
        miscFees.putIfAbsent(sn, loadMiscFeesFromFile());
        waivedAmounts.putIfAbsent(sn, 0.0);
        refreshPendingStudentIds();
        syncStudentToAdminAndPortal(sn);
        return sn;
    }

    public Student findStudent(String studentNumber) {
        if (studentNumber == null) return null;
        for (Student s : studentRepo.findAll()) {
            if (s.getStudentNumber().equalsIgnoreCase(studentNumber.trim())) return s;
        }
        return null;
    }

    public Student resolveStudentForPortal(String linkedId) {
        Student direct = findStudent(linkedId);
        if (direct != null) return direct;
        List<Student> all = studentRepo.findAll();
        return all.isEmpty() ? null : all.get(all.size() - 1);
    }

    public List<Student> getStudents() { return studentRepo.findAll(); }

    public boolean addSubject(String code, String title, int units, double feePerUnit, String instructor) {
        String subjCode = code.trim().toUpperCase();
        List<Subject> subjects = subjectRepo.findAll();
        for (Subject s : subjects) {
            if (s.getCode().equalsIgnoreCase(subjCode)) return false;
        }
        String inst = (instructor == null || instructor.isBlank())
                ? DEFAULT_INSTRUCTORS.get(subjects.size() % DEFAULT_INSTRUCTORS.size()) : instructor.trim();
        subjects.add(new Subject(subjCode, title.trim(), units, feePerUnit, inst));
        subjectRepo.saveAll(subjects);
        return true;
    }

    public List<Subject> getSubjects() {
        assignDefaultInstructorsToUnassignedSubjects();
        return subjectRepo.findAll();
    }

    public Subject findSubject(String code) {
        if (code == null) return null;
        for (Subject s : getSubjects()) {
            if (s.getCode().equalsIgnoreCase(code.trim())) return s;
        }
        return null;
    }

    public List<Subject> getSubjectsForInstructor(String instructorId) {
        List<Subject> list = new ArrayList<>();
        for (Subject s : getSubjects()) {
            if (s.getInstructor().equalsIgnoreCase(instructorId.trim())) list.add(s);
        }
        return list;
    }

    public String enrollStudent(String studentNumber, String subjectCode, String semester) {
        String sn = studentNumber.trim(), sc = subjectCode.trim().toUpperCase();
        if (findStudent(sn) == null) return "Student not found.";
        if (findSubject(sc) == null) return "Subject not found.";
        List<EnrollmentRecord> records = enrollmentRepo.findAll();
        for (EnrollmentRecord r : records) {
            if (r.getStudentNumber().equalsIgnoreCase(sn) && r.getSubjectCode().equalsIgnoreCase(sc)
                    && r.getSemester().equalsIgnoreCase(semester.trim())) {
                return "Student is already enrolled in this subject for the given semester.";
            }
        }
        String id = "ENR-" + (records.size() + 1) + "-" + System.currentTimeMillis() % 10000;
        records.add(new EnrollmentRecord(id, sn, sc, semester.trim(), "ENROLLED", "N/A"));
        enrollmentRepo.saveAll(records);
        syncStudentToAdminAndPortal(sn);
        return "Enrollment successful. Record ID: " + id;
    }

    public List<EnrollmentRecord> getEnrollmentsByStudent(String studentNumber) {
        List<EnrollmentRecord> list = new ArrayList<>();
        for (EnrollmentRecord r : enrollmentRepo.findAll()) {
            if (r.getStudentNumber().equalsIgnoreCase(studentNumber.trim())) list.add(r);
        }
        return list;
    }

    public List<String> getAdminEnrollmentAndInstructorReport() {
        assignDefaultInstructorsToUnassignedSubjects();
        List<String> out = new ArrayList<>();
        Set<String> seen = new LinkedHashSet<>();
        int i = 0;
        for (Student st : studentRepo.findAll()) {
            List<EnrollmentRecord> recs = getEnrollmentsByStudent(st.getStudentNumber());
            if (recs.isEmpty()) {
                String line = "[NOT ENROLLED] " + st.getStudentNumber() + " - " + st.getFullName()
                        + " (" + st.getCourse() + " Yr " + st.getYearLevel() + ") | No enrolled subjects";
                if (seen.add(line)) out.add(line);
            } else {
                for (EnrollmentRecord r : recs) {
                    Subject subj = findSubject(r.getSubjectCode());
                    String title = subj != null ? subj.getTitle() : r.getSubjectCode();
                    String line = "[ENROLLED] " + st.getStudentNumber() + " - " + st.getFullName()
                            + " | Subject: " + r.getSubjectCode() + " (" + title + ")"
                            + " | Instructor: " + resolveInstructor(subj, i++)
                            + " | Period: " + r.getSemester() + " | Grade: " + r.getGrade();
                    if (seen.add(line)) out.add(line);
                }
            }
        }
        return out.isEmpty() ? List.of("No students or enrollment records found.") : out;
    }

    public List<String> getRegistrarEnrollmentSummary(String filterStudentNo) {
        List<String> out = new ArrayList<>();
        Set<String> seen = new LinkedHashSet<>();
        List<Student> targets = (filterStudentNo != null && !filterStudentNo.isBlank())
                ? (findStudent(filterStudentNo.trim()) != null ? List.of(findStudent(filterStudentNo.trim())) : List.of())
                : studentRepo.findAll();

        for (Student st : targets) {
            List<EnrollmentRecord> recs = getEnrollmentsByStudent(st.getStudentNumber());
            if (recs.isEmpty()) {
                String line = "[NOT ENROLLED] " + st.getStudentNumber() + " - " + st.getFullName()
                        + " (" + st.getCourse() + " Yr " + st.getYearLevel() + ") | No subjects enrolled";
                if (seen.add(line)) out.add(line);
            } else {
                for (EnrollmentRecord r : recs) {
                    String line = "[ENROLLED] " + st.getStudentNumber() + " - " + st.getFullName()
                            + " | Subject: " + r.getSubjectCode() + " | Period: " + r.getSemester() + " | Status: " + r.getStatus();
                    if (seen.add(line)) out.add(line);
                }
            }
        }
        return out.isEmpty() ? List.of("[NOT ENROLLED] No student enrollment records found.") : out;
    }

    public List<String> getStudentEnrolledSubjectsWithInstructor(String studentNumber) {
        assignDefaultInstructorsToUnassignedSubjects();
        List<String> lines = new ArrayList<>();
        int i = 0;
        for (EnrollmentRecord r : getEnrollmentsByStudent(studentNumber)) {
            Subject subj = findSubject(r.getSubjectCode());
            String title = subj != null ? subj.getTitle() : r.getSubjectCode();
            lines.add("[ENROLLED] " + r.getSubjectCode() + " - " + title
                    + " | Instructor: " + resolveInstructor(subj, i++)
                    + " | Period: " + r.getSemester() + " | Grade: " + r.getGrade() + " (" + r.getStatus() + ")");
        }
        return lines;
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
                syncStudentToAdminAndPortal(r.getStudentNumber());
                return true;
            }
        }
        return false;
    }

    public double calculateTotalAssessment(String studentNumber) {
        double tuition = 0.0, fallback = getTuitionRateFromFile();
        Map<String, Subject> map = new HashMap<>();
        for (Subject s : getSubjects()) map.put(s.getCode().toUpperCase(), s);
        for (EnrollmentRecord r : getEnrollmentsByStudent(studentNumber)) {
            Subject s = map.get(r.getSubjectCode().toUpperCase());
            if (s != null) tuition += s.getUnits() * (s.getFeePerUnit() > 0 ? s.getFeePerUnit() : fallback);
        }
        return tuition;
    }

    public void setAdditionalFees(String studentNumber, double labFee, double miscFee) {
        labFees.put(studentNumber.trim(), Math.max(0.0, labFee));
        miscFees.put(studentNumber.trim(), Math.max(0.0, miscFee));
        syncStudentToAdminAndPortal(studentNumber.trim());
    }

    public void waiveFee(String studentNumber, double amount) {
        String sn = studentNumber.trim();
        waivedAmounts.put(sn, waivedAmounts.getOrDefault(sn, 0.0) + Math.max(0.0, amount));
        syncStudentToAdminAndPortal(sn);
    }

    public double getLabFee(String studentNumber) { return labFees.getOrDefault(studentNumber.trim(), DEFAULT_LAB_FEE); }
    public double getMiscFee(String studentNumber) { return miscFees.getOrDefault(studentNumber.trim(), loadMiscFeesFromFile()); }
    public double getWaivedAmount(String studentNumber) { return waivedAmounts.getOrDefault(studentNumber.trim(), 0.0); }

    public double calculateTotalPaid(String studentNumber) {
        double paid = 0.0;
        for (Payment p : getPaymentsByStudent(studentNumber)) paid += p.getAmount();
        return paid;
    }

    public double calculateBalance(String studentNumber) {
        double total = Math.max(0.0, calculateTotalAssessment(studentNumber) + getLabFee(studentNumber)
                + getMiscFee(studentNumber) - getWaivedAmount(studentNumber));
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
        syncStudentToAdminAndPortal(sn);
        return "Payment recorded. Receipt ID: " + id + " | Record synced to Admin & Student Portal.";
    }

    public List<Payment> getPaymentsByStudent(String studentNumber) {
        List<Payment> list = new ArrayList<>();
        for (Payment p : paymentRepo.findAll()) {
            if (p.getStudentNumber().equalsIgnoreCase(studentNumber.trim())) list.add(p);
        }
        return list;
    }

    public String submitShiftingRequest(String studentNumber, String targetCourse, String reason) {
        Student st = findStudent(studentNumber.trim());
        if (st == null) return "Student record not found.";
        List<ShiftingRequest> list = shiftingRepo.findAll();
        String id = "SHF-" + (list.size() + 1) + "-" + System.currentTimeMillis() % 10000;
        list.add(new ShiftingRequest(id, st.getStudentNumber(), st.getCourse(), targetCourse.trim().toUpperCase(), reason.trim(), "PENDING"));
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
                            syncStudentToAdminAndPortal(s.getStudentNumber());
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