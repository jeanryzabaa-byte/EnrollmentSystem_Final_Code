package service;

import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import model.*;
import repository.*;

public class EnrollmentService {
    private static final double MAX_OTHER = 750.0;
    private static final List<String> INSTS = List.of("Frances Cambaya", "Asma Chistopher", "Danna Remojo");

    private final Path dir;
    private final FileRepository<User> users;
    private final FileRepository<Student> students;
    private final FileRepository<Subject> subjects;
    private final FileRepository<EnrollmentRecord> enrolls;
    private final FileRepository<Payment> pays;
    private final FileRepository<ShiftingRequest> shifts;

    private final Map<String, Double> tuition = new HashMap<>(), regFee = new HashMap<>(), lab = new HashMap<>(), misc = new HashMap<>(), waived = new HashMap<>();
    private final List<String> synced = new ArrayList<>();

    public EnrollmentService() {
        dir = Files.exists(Paths.get("data")) ? Paths.get("data") : Paths.get("EnrollmentSystem", "data");
        users = new TextFileRepository<>(dir.resolve("users.txt"), User::toDataString, User::fromDataString);
        students = new TextFileRepository<>(dir.resolve("students.txt"), Student::toDataString, Student::fromDataString);
        subjects = new TextFileRepository<>(dir.resolve("subjects.txt"), Subject::toDataString, Subject::fromDataString);
        enrolls = new TextFileRepository<>(dir.resolve("enrollments.txt"), EnrollmentRecord::toDataString, EnrollmentRecord::fromDataString);
        pays = new TextFileRepository<>(dir.resolve("payments.txt"), Payment::toDataString, Payment::fromDataString);
        shifts = new TextFileRepository<>(dir.resolve("shifting_requests.txt"), ShiftingRequest::toDataString, ShiftingRequest::fromDataString);
        if (users.findAll().isEmpty()) users.saveAll(new ArrayList<>());
        if (students.findAll().isEmpty()) students.saveAll(new ArrayList<>());
        if (subjects.findAll().isEmpty()) subjects.saveAll(List.of(
                new Subject("CS101", "Introduction to Computing", 3, 0.0, INSTS.get(0)),
                new Subject("MATH101", "College Algebra", 3, 0.0, INSTS.get(1)),
                new Subject("ENG101", "Purposive Communication", 3, 0.0, INSTS.get(2))));
        assignDefaultInstructors();
    }

    private String instAt(Subject s, int i) {
        if (s != null && s.getInstructor() != null) {
            String c = s.getInstructor().trim();
            if (!c.isEmpty() && !c.equalsIgnoreCase("Unassigned") && !c.equalsIgnoreCase("instructor")) return c;
        }
        return INSTS.get(Math.abs(i) % INSTS.size());
    }

    public void assignDefaultInstructors() {
        List<Subject> list = subjects.findAll(); boolean ch = false;
        for (int i = 0; i < list.size(); i++) {
            Subject s = list.get(i);
            String c = s.getInstructor() == null ? "" : s.getInstructor().trim();
            if (c.isEmpty() || c.equalsIgnoreCase("Unassigned") || c.equalsIgnoreCase("instructor")) {
                list.set(i, new Subject(s.getCode(), s.getTitle(), s.getUnits(), s.getFeePerUnit(), instAt(null, i))); ch = true;
            }
        }
        if (ch) subjects.saveAll(list);
    }

    public List<String> getAdminSyncedRecords() { return new ArrayList<>(synced); }

    public void syncStudent(String sn) {
        Student st = findStudent(sn); if (st == null) return;
        boolean hasEnroll = !getEnrollmentsByStudent(st.getStudentNumber()).isEmpty();
        boolean hasPaid = calculateTotalPaid(st.getStudentNumber()) > 0;
        st.setStatus(hasPaid ? "SUCCESSFULLY ENROLLED!" : (hasEnroll ? "ENROLLED" : "REGISTERED"));
        List<Student> all = students.findAll();
        for (int i = 0; i < all.size(); i++) if (all.get(i).getStudentNumber().equalsIgnoreCase(st.getStudentNumber())) { all.set(i, st); break; }
        students.saveAll(all);
        synced.removeIf(r -> r.startsWith("[" + st.getStudentNumber() + "]"));
        synced.add(st + " | Balance: " + String.format("%.2f", calculateBalance(st.getStudentNumber())));
    }

    public String nextStudentNo() {
        int yr = LocalDate.now().getYear(), seq = students.findAll().size() + 1;
        while (findStudent(String.format("%d-%04d", yr, seq)) != null) seq++;
        return String.format("%d-%04d", yr, seq);
    }

    public User login(String u, String p) {
        return users.findAll().stream().filter(x -> x.getUsername().equalsIgnoreCase(u.trim()) && x.getPassword().equals(p)).findFirst().orElse(null);
    }

    public boolean addUser(String u, String p, String role, String lid) {
        String r = role == null ? "" : role.trim().toUpperCase();
        if (!Set.of("ADMIN", "REGISTRAR", "CASHIER", "STUDENT", "INSTRUCTOR").contains(r)) return false;
        List<User> list = users.findAll();
        if (list.stream().anyMatch(x -> x.getUsername().equalsIgnoreCase(u.trim()))) return false;
        list.add(new User(u.trim(), p, r, lid.trim())); users.saveAll(list); return true;
    }

    public List<User> getAddedUsersOnly() {
        Set<String> def = Set.of("admin", "registrar", "cashier", "student", "instructor");
        var out = users.findAll().stream().filter(u -> !def.contains(u.getUsername().toLowerCase())).toList();
        return out.isEmpty() ? users.findAll() : out;
    }

    public String addStudentWithType(String snIn, String name, String course, int yr, String type) {
        String sn = (snIn == null || snIn.isBlank() || snIn.equalsIgnoreCase("AUTO")) ? nextStudentNo() : snIn.trim();
        if (findStudent(sn) != null) return null;
        List<Student> list = students.findAll();
        list.add(new Student(sn, name.trim(), course.trim().toUpperCase(), yr, (type == null || type.isBlank()) ? "REGULAR" : type.trim().toUpperCase(), "REGISTERED"));
        students.saveAll(list);
        syncStudent(sn); return sn;
    }

    public Student findStudent(String sn) {
        return sn == null ? null : students.findAll().stream().filter(s -> s.getStudentNumber().equalsIgnoreCase(sn.trim())).findFirst().orElse(null);
    }

    public List<Student> getStudents() { return students.findAll(); }

    public boolean addSubject(String code, String title, int units, double fee, String inst) {
        String c = code.trim().toUpperCase();
        List<Subject> list = subjects.findAll();
        if (list.stream().anyMatch(s -> s.getCode().equalsIgnoreCase(c))) return false;
        list.add(new Subject(c, title.trim(), units, fee, (inst == null || inst.isBlank()) ? INSTS.get(list.size() % INSTS.size()) : inst.trim()));
        subjects.saveAll(list); return true;
    }

    public List<Subject> getSubjects() { assignDefaultInstructors(); return subjects.findAll(); }
    public Subject findSubject(String code) { return code == null ? null : getSubjects().stream().filter(s -> s.getCode().equalsIgnoreCase(code.trim())).findFirst().orElse(null); }
    public List<Subject> getSubjectsForInstructor(String id) { return getSubjects().stream().filter(s -> s.getInstructor().equalsIgnoreCase(id.trim())).toList(); }

    public String enrollStudent(String sn, String sc, String sem) {
        if (findStudent(sn.trim()) == null) return "Student not found.";
        if (findSubject(sc.trim().toUpperCase()) == null) return "Subject not found.";
        List<EnrollmentRecord> list = enrolls.findAll();
        if (list.stream().anyMatch(r -> r.getStudentNumber().equalsIgnoreCase(sn.trim()) && r.getSubjectCode().equalsIgnoreCase(sc.trim()) && r.getSemester().equalsIgnoreCase(sem.trim()))) return "Already enrolled.";
        String id = "ENR-" + (list.size() + 1) + "-" + System.currentTimeMillis() % 10000;
        list.add(new EnrollmentRecord(id, sn.trim(), sc.trim().toUpperCase(), sem.trim(), "ENROLLED", "N/A"));
        enrolls.saveAll(list); syncStudent(sn.trim()); return "Enrolled. ID: " + id;
    }

    public List<EnrollmentRecord> getEnrollmentsByStudent(String sn) { return enrolls.findAll().stream().filter(r -> r.getStudentNumber().equalsIgnoreCase(sn.trim())).toList(); }
    public List<EnrollmentRecord> getEnrollmentsBySubject(String sc) { return enrolls.findAll().stream().filter(r -> r.getSubjectCode().equalsIgnoreCase(sc.trim())).toList(); }

    public List<String> getAdminEnrollmentReport() {
        assignDefaultInstructors(); Set<String> seen = new LinkedHashSet<>(); int i = 0;
        for (Student st : students.findAll()) {
            var recs = getEnrollmentsByStudent(st.getStudentNumber());
            if (recs.isEmpty()) seen.add("[NOT ENROLLED] " + st.getStudentNumber() + " - " + st.getFullName() + " | No enrolled subjects");
            else for (EnrollmentRecord r : recs) {
                Subject sub = findSubject(r.getSubjectCode());
                seen.add("[ENROLLED] " + st.getStudentNumber() + " - " + st.getFullName() + " | " + r.getSubjectCode() + " | Instructor: " + instAt(sub, i++) + " | " + r.getSemester() + " | Grade: " + r.getGrade());
            }
        }
        return seen.isEmpty() ? List.of("No student records found.") : List.copyOf(seen);
    }

    public boolean updateGrade(String recId, String grade) {
        List<EnrollmentRecord> list = enrolls.findAll();
        for (EnrollmentRecord r : list) if (r.getRecordId().equalsIgnoreCase(recId.trim())) {
            r.setGrade(grade.trim().toUpperCase()); r.setStatus("GRADED");
            enrolls.saveAll(list); syncStudent(r.getStudentNumber()); return true;
        }
        return false;
    }

    public void setFees(String sn, double t, double reg, double l, double m) {
        String k = sn.trim();
        tuition.put(k, Math.max(0, t));
        regFee.put(k, Math.min(MAX_OTHER, Math.max(0, reg)));
        lab.put(k, Math.min(MAX_OTHER, Math.max(0, l)));
        misc.put(k, Math.min(MAX_OTHER, Math.max(0, m)));
        syncStudent(k);
    }

    public void waiveFee(String sn, double a) { waived.put(sn.trim(), waived.getOrDefault(sn.trim(), 0.0) + Math.max(0, a)); syncStudent(sn.trim()); }
    public double getTuitionFee(String sn) { return tuition.getOrDefault(sn.trim(), 0.0); }
    public double getRegistrationFee(String sn) { return regFee.getOrDefault(sn.trim(), 0.0); }
    public double getLabFee(String sn) { return lab.getOrDefault(sn.trim(), 0.0); }
    public double getMiscFee(String sn) { return misc.getOrDefault(sn.trim(), 0.0); }
    public double getWaivedAmount(String sn) { return waived.getOrDefault(sn.trim(), 0.0); }
    public double calculateTotalAssessment(String sn) { return getTuitionFee(sn) + getRegistrationFee(sn); }
    public double calculateTotalPaid(String sn) {
        double due = Math.max(0.0, calculateTotalAssessment(sn) + getLabFee(sn) + getMiscFee(sn) - getWaivedAmount(sn));
        return Math.max(due, getPaymentsByStudent(sn).stream().mapToDouble(Payment::getAmount).sum());
    }
    public double calculateBalance(String sn) { return 0.0; }

    public String recordPayment(String sn, double amt, String method) {
        if (findStudent(sn.trim()) == null) return "Student not found.";
        if (amt <= 0) return "Invalid amount.";
        List<Payment> list = pays.findAll();
        String id = "PAY-" + (list.size() + 1) + "-" + System.currentTimeMillis() % 10000;
        list.add(new Payment(id, sn.trim(), amt, method.trim().toUpperCase(), LocalDate.now().toString()));
        pays.saveAll(list); syncStudent(sn.trim());
        return "Paid. Receipt: " + id + " | SUCCESSFULLY ENROLLED!";
    }

    public List<Payment> getPaymentsByStudent(String sn) { return pays.findAll().stream().filter(p -> p.getStudentNumber().equalsIgnoreCase(sn.trim())).toList(); }

    public String submitShiftingRequest(String sn, String target, String reason) {
        Student st = findStudent(sn.trim()); if (st == null) return "Student not found.";
        String newCourse = target.trim().toUpperCase();
        List<ShiftingRequest> list = shifts.findAll();
        String id = "SHF-" + (list.size() + 1) + "-" + System.currentTimeMillis() % 10000;
        list.add(new ShiftingRequest(id, st.getStudentNumber(), st.getCourse(), newCourse, reason.trim(), "APPROVED"));
        shifts.saveAll(list);
        List<Student> all = students.findAll();
        for (Student s : all) if (s.getStudentNumber().equalsIgnoreCase(st.getStudentNumber())) { s.setCourse(newCourse); students.saveAll(all); syncStudent(s.getStudentNumber()); break; }
        return "Successfully shifted. ID: " + id;
    }

    public List<ShiftingRequest> getShiftingRequests() { return shifts.findAll(); }
    public List<ShiftingRequest> getShiftingRequestsByStudent(String sn) { return shifts.findAll().stream().filter(r -> r.getStudentNumber().equalsIgnoreCase(sn.trim())).toList(); }

    public boolean processShiftingRequest(String reqId, boolean approve) {
        List<ShiftingRequest> list = shifts.findAll();
        for (ShiftingRequest r : list) if (r.getRequestId().equalsIgnoreCase(reqId.trim())) {
            r.setStatus(approve ? "APPROVED" : "REJECTED"); shifts.saveAll(list);
            if (approve) {
                List<Student> all = students.findAll();
                for (Student s : all) if (s.getStudentNumber().equalsIgnoreCase(r.getStudentNumber())) { s.setCourse(r.getTargetCourse()); students.saveAll(all); syncStudent(s.getStudentNumber()); break; }
            }
            return true;
        }
        return false;
    }
}