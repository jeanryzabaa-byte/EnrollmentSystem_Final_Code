package service;

import model.*;
import repository.*;

import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

public class EnrollmentService {
    private static final double DEF_LAB = 1000.0, DEF_MISC = 1500.0;
    private static final List<String> INSTRUCTORS = List.of("Frances Cambaya", "Asma Chistopher", "Danna Remojo");

    private final Path dataDir;
    private final FileRepository<User> userRepo;
    private final FileRepository<Student> studentRepo;
    private final FileRepository<Subject> subjectRepo;
    private final FileRepository<EnrollmentRecord> enrollRepo;
    private final FileRepository<Payment> payRepo;
    private final FileRepository<ShiftingRequest> shiftRepo;

    private final Map<String, Double> labFees = new HashMap<>(), miscFees = new HashMap<>(), waived = new HashMap<>();
    private final List<String> synced = new ArrayList<>();

    public EnrollmentService() {
        dataDir = Files.exists(Paths.get("data")) ? Paths.get("data") : Paths.get("EnrollmentSystem", "data");
        userRepo = new TextFileRepository<>(dataDir.resolve("users.txt"), User::toDataString, User::fromDataString);
        studentRepo = new TextFileRepository<>(dataDir.resolve("students.txt"), Student::toDataString, Student::fromDataString);
        subjectRepo = new TextFileRepository<>(dataDir.resolve("subjects.txt"), Subject::toDataString, Subject::fromDataString);
        enrollRepo = new TextFileRepository<>(dataDir.resolve("enrollments.txt"), EnrollmentRecord::toDataString, EnrollmentRecord::fromDataString);
        payRepo = new TextFileRepository<>(dataDir.resolve("payments.txt"), Payment::toDataString, Payment::fromDataString);
        shiftRepo = new TextFileRepository<>(dataDir.resolve("shifting_requests.txt"), ShiftingRequest::toDataString, ShiftingRequest::fromDataString);

        if (userRepo.findAll().isEmpty()) userRepo.saveAll(new ArrayList<>());
        if (studentRepo.findAll().isEmpty()) studentRepo.saveAll(new ArrayList<>());
        if (subjectRepo.findAll().isEmpty()) {
            subjectRepo.saveAll(List.of(
                    new Subject("CS101", "Introduction to Computing", 3, 150.0, "Frances Cambaya"),
                    new Subject("MATH101", "College Algebra", 3, 150.0, "Asma Chistopher"),
                    new Subject("ENG101", "Purposive Communication", 3, 150.0, "Danna Remojo")
            ));
        }
        assignDefaultInstructors();
    }

    private double readFeesFile(String keyTarget, boolean sumMisc, double fallback) {
        Path f = dataDir.resolve("fees.txt");
        if (!Files.exists(f)) return fallback;
        double sum = 0.0;
        boolean found = false;
        try {
            for (String line : Files.readAllLines(f)) {
                if (line == null || line.isBlank()) continue;
                String[] p = line.split("\\|");
                if (p.length < 2) continue;
                String k = p[0].trim().toUpperCase();
                double v = Double.parseDouble(p[1].trim());
                if (!sumMisc && k.equals(keyTarget)) return v;
                if (sumMisc && !k.equals("TUITION_PER_UNIT")) { sum += v; found = true; }
            }
        } catch (Exception ignored) { }
        return sumMisc ? (found ? sum : fallback) : fallback;
    }

    public double loadMiscFeesFromFile() { return readFeesFile("", true, DEF_MISC); }
    public double getTuitionRateFromFile() { return readFeesFile("TUITION_PER_UNIT", false, 150.0); }

    private String pickInstructor(Subject s, int idx) {
        if (s != null && s.getInstructor() != null) {
            String cur = s.getInstructor().trim();
            if (!cur.isEmpty() && !cur.equalsIgnoreCase("Unassigned") && !cur.equalsIgnoreCase("instructor")) return cur;
        }
        return INSTRUCTORS.get(Math.abs(idx) % INSTRUCTORS.size());
    }

    public void assignDefaultInstructors() {
        List<Subject> subs = subjectRepo.findAll();
        boolean changed = false;
        for (int i = 0; i < subs.size(); i++) {
            Subject s = subs.get(i);
            String cur = s.getInstructor() == null ? "" : s.getInstructor().trim();
            if (cur.isEmpty() || cur.equalsIgnoreCase("Unassigned") || cur.equalsIgnoreCase("instructor")) {
                subs.set(i, new Subject(s.getCode(), s.getTitle(), s.getUnits(), s.getFeePerUnit(), pickInstructor(null, i)));
                changed = true;
            }
        }
        if (changed) subjectRepo.saveAll(subs);
    }

    public List<String> getAdminSyncedRecords() { return new ArrayList<>(synced); }

    public void syncStudent(String sn) {
        Student st = findStudent(sn);
        if (st == null) return;
        st.setStatus(getEnrollmentsByStudent(st.getStudentNumber()).isEmpty() ? "REGISTERED" : "ENROLLED");
        List<Student> all = studentRepo.findAll();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getStudentNumber().equalsIgnoreCase(st.getStudentNumber())) { all.set(i, st); break; }
        }
        studentRepo.saveAll(all);
        String line = st + " | Balance: " + String.format("%.2f", calculateBalance(st.getStudentNumber()));
        synced.removeIf(r -> r.startsWith("[" + st.getStudentNumber() + "]"));
        synced.add(line);
    }

    public String nextStudentNo() {
        int yr = LocalDate.now().getYear(), seq = studentRepo.findAll().size() + 1;
        while (findStudent(String.format("%d-%04d", yr, seq)) != null) seq++;
        return String.format("%d-%04d", yr, seq);
    }

    public User login(String u, String p) {
        if (u == null || p == null) return null;
        for (User x : userRepo.findAll()) {
            if (x.getUsername().equalsIgnoreCase(u.trim()) && x.getPassword().equals(p)) return x;
        }
        return null;
    }

    public boolean addUser(String u, String p, String role, String lid) {
        String r = role == null ? "" : role.trim().toUpperCase();
        if (r.equals("SYSTEM ADMINISTRATOR")) r = "ADMIN";
        if (!Set.of("ADMIN", "REGISTRAR", "CASHIER", "STUDENT", "INSTRUCTOR").contains(r)) return false;
        for (User x : userRepo.findAll()) if (x.getUsername().equalsIgnoreCase(u.trim())) return false;
        List<User> list = userRepo.findAll();
        list.add(new User(u.trim(), p, r, lid.trim()));
        userRepo.saveAll(list);
        return true;
    }

    public List<User> getAddedUsersOnly() {
        Set<String> def = Set.of("admin", "registrar", "cashier", "student", "instructor");
        List<User> out = new ArrayList<>();
        for (User u : userRepo.findAll()) if (!def.contains(u.getUsername().toLowerCase())) out.add(u);
        return out.isEmpty() ? userRepo.findAll() : out;
    }

    public String addStudentWithType(String snIn, String name, String course, int yr, String type) {
        String sn = (snIn == null || snIn.isBlank() || snIn.equalsIgnoreCase("AUTO")) ? nextStudentNo() : snIn.trim();
        if (findStudent(sn) != null) return null;
        List<Student> list = studentRepo.findAll();
        list.add(new Student(sn, name.trim(), course.trim().toUpperCase(), yr,
                (type == null || type.isBlank()) ? "REGULAR" : type.trim().toUpperCase(), "REGISTERED"));
        studentRepo.saveAll(list);
        labFees.putIfAbsent(sn, DEF_LAB);
        miscFees.putIfAbsent(sn, loadMiscFeesFromFile());
        waived.putIfAbsent(sn, 0.0);
        syncStudent(sn);
        return sn;
    }

    public Student findStudent(String sn) {
        if (sn == null) return null;
        for (Student s : studentRepo.findAll()) if (s.getStudentNumber().equalsIgnoreCase(sn.trim())) return s;
        return null;
    }

    public Student resolveStudentForPortal(String lid) {
        Student d = findStudent(lid);
        if (d != null) return d;
        List<Student> all = studentRepo.findAll();
        return all.isEmpty() ? null : all.get(all.size() - 1);
    }

    public List<Student> getStudents() { return studentRepo.findAll(); }

    public boolean addSubject(String code, String title, int units, double fee, String inst) {
        String c = code.trim().toUpperCase();
        List<Subject> subs = subjectRepo.findAll();
        for (Subject s : subs) if (s.getCode().equalsIgnoreCase(c)) return false;
        String ins = (inst == null || inst.isBlank()) ? INSTRUCTORS.get(subs.size() % INSTRUCTORS.size()) : inst.trim();
        subs.add(new Subject(c, title.trim(), units, fee, ins));
        subjectRepo.saveAll(subs);
        return true;
    }

    public List<Subject> getSubjects() { assignDefaultInstructors(); return subjectRepo.findAll(); }

    public Subject findSubject(String code) {
        if (code == null) return null;
        for (Subject s : getSubjects()) if (s.getCode().equalsIgnoreCase(code.trim())) return s;
        return null;
    }

    public List<Subject> getSubjectsForInstructor(String instId) {
        List<Subject> out = new ArrayList<>();
        for (Subject s : getSubjects()) if (s.getInstructor().equalsIgnoreCase(instId.trim())) out.add(s);
        return out;
    }

    public String enrollStudent(String sn, String sc, String sem) {
        if (findStudent(sn.trim()) == null) return "Student not found.";
        if (findSubject(sc.trim().toUpperCase()) == null) return "Subject not found.";
        List<EnrollmentRecord> list = enrollRepo.findAll();
        for (EnrollmentRecord r : list) {
            if (r.getStudentNumber().equalsIgnoreCase(sn.trim()) && r.getSubjectCode().equalsIgnoreCase(sc.trim())
                    && r.getSemester().equalsIgnoreCase(sem.trim())) return "Already enrolled in this subject.";
        }
        String id = "ENR-" + (list.size() + 1) + "-" + System.currentTimeMillis() % 10000;
        list.add(new EnrollmentRecord(id, sn.trim(), sc.trim().toUpperCase(), sem.trim(), "ENROLLED", "N/A"));
        enrollRepo.saveAll(list);
        syncStudent(sn.trim());
        return "Enrollment successful. Record ID: " + id;
    }

    public List<EnrollmentRecord> getEnrollmentsByStudent(String sn) {
        List<EnrollmentRecord> out = new ArrayList<>();
        for (EnrollmentRecord r : enrollRepo.findAll()) if (r.getStudentNumber().equalsIgnoreCase(sn.trim())) out.add(r);
        return out;
    }

    public List<EnrollmentRecord> getEnrollmentsBySubject(String sc) {
        List<EnrollmentRecord> out = new ArrayList<>();
        for (EnrollmentRecord r : enrollRepo.findAll()) if (r.getSubjectCode().equalsIgnoreCase(sc.trim())) out.add(r);
        return out;
    }

    public List<String> getAdminEnrollmentReport() {
        assignDefaultInstructors();
        List<String> out = new ArrayList<>();
        Set<String> seen = new LinkedHashSet<>();
        int i = 0;
        for (Student st : studentRepo.findAll()) {
            var recs = getEnrollmentsByStudent(st.getStudentNumber());
            if (recs.isEmpty()) {
                String l = "[NOT ENROLLED] " + st.getStudentNumber() + " - " + st.getFullName() + " | No enrolled subjects";
                if (seen.add(l)) out.add(l);
            } else {
                for (EnrollmentRecord r : recs) {
                    Subject sub = findSubject(r.getSubjectCode());
                    String l = "[ENROLLED] " + st.getStudentNumber() + " - " + st.getFullName()
                            + " | " + r.getSubjectCode() + " | Instructor: " + pickInstructor(sub, i++)
                            + " | " + r.getSemester() + " | Grade: " + r.getGrade();
                    if (seen.add(l)) out.add(l);
                }
            }
        }
        return out.isEmpty() ? List.of("No student records found.") : out;
    }

    public List<String> getStudentSubjectsWithInstructor(String sn) {
        assignDefaultInstructors();
        List<String> out = new ArrayList<>();
        int i = 0;
        for (EnrollmentRecord r : getEnrollmentsByStudent(sn)) {
            Subject sub = findSubject(r.getSubjectCode());
            out.add("[ENROLLED] " + r.getSubjectCode() + (sub != null ? " - " + sub.getTitle() : "")
                    + " | Instructor: " + pickInstructor(sub, i++) + " | " + r.getSemester() + " | Grade: " + r.getGrade());
        }
        return out;
    }

    public boolean updateGrade(String recId, String grade) {
        List<EnrollmentRecord> list = enrollRepo.findAll();
        for (EnrollmentRecord r : list) {
            if (r.getRecordId().equalsIgnoreCase(recId.trim())) {
                r.setGrade(grade.trim().toUpperCase());
                r.setStatus("GRADED");
                enrollRepo.saveAll(list);
                syncStudent(r.getStudentNumber());
                return true;
            }
        }
        return false;
    }

    public double calculateTotalAssessment(String sn) {
        double t = 0.0, rate = getTuitionRateFromFile();
        Map<String, Subject> map = new HashMap<>();
        for (Subject s : getSubjects()) map.put(s.getCode().toUpperCase(), s);
        for (EnrollmentRecord r : getEnrollmentsByStudent(sn)) {
            Subject s = map.get(r.getSubjectCode().toUpperCase());
            if (s != null) t += s.getUnits() * (s.getFeePerUnit() > 0 ? s.getFeePerUnit() : rate);
        }
        return t;
    }

    public void setAdditionalFees(String sn, double lab, double misc) {
        labFees.put(sn.trim(), Math.max(0.0, lab));
        miscFees.put(sn.trim(), Math.max(0.0, misc));
        syncStudent(sn.trim());
    }

    public void waiveFee(String sn, double amt) {
        waived.put(sn.trim(), waived.getOrDefault(sn.trim(), 0.0) + Math.max(0.0, amt));
        syncStudent(sn.trim());
    }

    public double getLabFee(String sn) { return labFees.getOrDefault(sn.trim(), DEF_LAB); }
    public double getMiscFee(String sn) { return miscFees.getOrDefault(sn.trim(), loadMiscFeesFromFile()); }
    public double getWaivedAmount(String sn) { return waived.getOrDefault(sn.trim(), 0.0); }

    public double calculateTotalPaid(String sn) {
        double p = 0.0;
        for (Payment x : getPaymentsByStudent(sn)) p += x.getAmount();
        return p;
    }

    public double calculateBalance(String sn) {
        double due = Math.max(0.0, calculateTotalAssessment(sn) + getLabFee(sn) + getMiscFee(sn) - getWaivedAmount(sn));
        return Math.max(0.0, due - calculateTotalPaid(sn));
    }

    public String recordPayment(String sn, double amt, String method) {
        if (findStudent(sn.trim()) == null) return "Student not found.";
        if (amt <= 0) return "Amount must be greater than zero.";
        List<Payment> list = payRepo.findAll();
        String id = "PAY-" + (list.size() + 1) + "-" + System.currentTimeMillis() % 10000;
        list.add(new Payment(id, sn.trim(), amt, method.trim().toUpperCase(), LocalDate.now().toString()));
        payRepo.saveAll(list);
        syncStudent(sn.trim());
        return "Payment recorded. Receipt ID: " + id;
    }

    public List<Payment> getPaymentsByStudent(String sn) {
        List<Payment> out = new ArrayList<>();
        for (Payment p : payRepo.findAll()) if (p.getStudentNumber().equalsIgnoreCase(sn.trim())) out.add(p);
        return out;
    }

    public String submitShiftingRequest(String sn, String target, String reason) {
        Student st = findStudent(sn.trim());
        if (st == null) return "Student record not found.";
        List<ShiftingRequest> list = shiftRepo.findAll();
        String id = "SHF-" + (list.size() + 1) + "-" + System.currentTimeMillis() % 10000;
        list.add(new ShiftingRequest(id, st.getStudentNumber(), st.getCourse(), target.trim().toUpperCase(), reason.trim(), "PENDING"));
        shiftRepo.saveAll(list);
        return "Shifting request submitted. Request ID: " + id;
    }

    public List<ShiftingRequest> getShiftingRequests() { return shiftRepo.findAll(); }

    public List<ShiftingRequest> getShiftingRequestsByStudent(String sn) {
        List<ShiftingRequest> out = new ArrayList<>();
        for (ShiftingRequest r : shiftRepo.findAll()) if (r.getStudentNumber().equalsIgnoreCase(sn.trim())) out.add(r);
        return out;
    }

    public boolean processShiftingRequest(String reqId, boolean approve) {
        List<ShiftingRequest> list = shiftRepo.findAll();
        for (ShiftingRequest r : list) {
            if (r.getRequestId().equalsIgnoreCase(reqId.trim())) {
                r.setStatus(approve ? "APPROVED" : "REJECTED");
                shiftRepo.saveAll(list);
                if (approve) {
                    List<Student> studs = studentRepo.findAll();
                    for (Student s : studs) {
                        if (s.getStudentNumber().equalsIgnoreCase(r.getStudentNumber())) {
                            s.setCourse(r.getTargetCourse());
                            studentRepo.saveAll(studs);
                            syncStudent(s.getStudentNumber());
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