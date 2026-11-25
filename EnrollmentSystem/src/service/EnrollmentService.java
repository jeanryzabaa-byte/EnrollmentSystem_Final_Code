public List<EnrollmentRecord> getAllEnrollments() {
        Map<String, EnrollmentRecord> unique = new LinkedHashMap<>();
        for (EnrollmentRecord r : enrollmentRepo.findAll()) {
            unique.put(r.getRecordId(), r);
        }
        return new ArrayList<>(unique.values());
    }

    public String getStudentEnrollmentStatus(String studentNumber) {
        List<EnrollmentRecord> list = getEnrollmentsByStudent(studentNumber);
        return list.isEmpty() ? "NOT ENROLLED" : "ENROLLED";
    }

    public List<String> getFormattedEnrollmentOverview(String filterStudentNo) {
        List<String> output = new ArrayList<>();
        Set<String> seenLines = new LinkedHashSet<>();

        List<Student> targetStudents = new ArrayList<>();
        if (filterStudentNo != null && !filterStudentNo.isBlank()) {
            Student found = findStudent(filterStudentNo.trim());
            if (found != null) {
                targetStudents.add(found);
            }
        } else {
            targetStudents.addAll(studentRepo.findAll());
        }

        for (Student st : targetStudents) {
            String sn = st.getStudentNumber();
            List<EnrollmentRecord> recs = getEnrollmentsByStudent(sn);
            String tag = recs.isEmpty() ? "[NOT ENROLLED]" : "[ENROLLED]";
            if (recs.isEmpty()) {
                String line = tag + " " + sn + " - " + st.getFullName() + " (" + st.getCourse() + " Yr " + st.getYearLevel() + ") | No subjects enrolled";
                if (seenLines.add(line)) output.add(line);
            } else {
                for (EnrollmentRecord r : recs) {
                    String line = tag + " " + sn + " - " + st.getFullName()
                            + " | Subject: " + r.getSubjectCode()
                            + " | Period: " + r.getSemester()
                            + " | Grade: " + r.getGrade();
                    if (seenLines.add(line)) output.add(line);
                }
            }
        }

        if (output.isEmpty() && filterStudentNo != null && !filterStudentNo.isBlank()) {
            List<EnrollmentRecord> recs = getEnrollmentsByStudent(filterStudentNo.trim());
            for (EnrollmentRecord r : recs) {
                String line = "[ENROLLED] " + r.getStudentNumber()
                        + " | Subject: " + r.getSubjectCode()
                        + " | Period: " + r.getSemester()
                        + " | Grade: " + r.getGrade();
                if (seenLines.add(line)) output.add(line);
            }
            if (output.isEmpty()) {
                output.add("[NOT ENROLLED] Student No. '" + filterStudentNo.trim() + "' has no enrollment records.");
            }
        }

        if (output.isEmpty()) {
            output.add("[NOT ENROLLED] No student enrollment records found yet.");
        }

        return output;
    }