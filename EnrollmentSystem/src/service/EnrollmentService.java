// Default Miscellaneous & Lab Fees + Waive tracking (in-memory/file-backed per student)
    private final java.util.Map<String, Double> labFees = new java.util.HashMap<>();
    private final java.util.Map<String, Double> miscFees = new java.util.HashMap<>();
    private final java.util.Map<String, Double> waivedAmounts = new java.util.HashMap<>();

    public boolean addStudentWithType(String studentNumber, String fullName, String course, int yearLevel, String studentType) {
        List<Student> students = studentRepo.findAll();
        for (Student s : students) {
            if (s.getStudentNumber().equalsIgnoreCase(studentNumber.trim())) return false;
        }
        students.add(new Student(studentNumber.trim(), fullName.trim(), course.trim().toUpperCase(), yearLevel, studentType, "ENROLLED"));
        studentRepo.saveAll(students);
        // Automatic default Miscellaneous Fee (1,500.00) ug Lab Fee (1,000.00)
        miscFees.put(studentNumber.trim(), 1500.00);
        labFees.put(studentNumber.trim(), 1000.00);
        return true;
    }

    public void setAdditionalFees(String studentNumber, double labFee, double miscFee) {
        labFees.put(studentNumber.trim(), Math.max(0, labFee));
        miscFees.put(studentNumber.trim(), Math.max(0, miscFee));
    }

    public void waiveFee(String studentNumber, double amount) {
        String sn = studentNumber.trim();
        double current = waivedAmounts.getOrDefault(sn, 0.0);
        waivedAmounts.put(sn, current + Math.max(0, amount));
    }

    public double getLabFee(String studentNumber) {
        return labFees.getOrDefault(studentNumber.trim(), 1000.00);
    }

    public double getMiscFee(String studentNumber) {
        return miscFees.getOrDefault(studentNumber.trim(), 1500.00);
    }

    public double getWaivedAmount(String studentNumber) {
        return waivedAmounts.getOrDefault(studentNumber.trim(), 0.0);
    }

    public double calculateGrandTotalAssessment(String studentNumber) {
        double tuition = calculateTotalAssessment(studentNumber);
        double lab = getLabFee(studentNumber);
        double misc = getMiscFee(studentNumber);
        double waived = getWaivedAmount(studentNumber);
        return Math.max(0.0, (tuition + lab + misc) - waived);
    }

    public double calculateNetBalance(String studentNumber) {
        return Math.max(0.0, calculateGrandTotalAssessment(studentNumber) - calculateTotalPaid(studentNumber));
    }