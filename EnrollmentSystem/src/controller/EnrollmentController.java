private void handleRegistrar() {
        while (true) {
            view.showRegistrarMenu();
            String c = input.readLine("Choice: ");
            switch (c) {
                case "1" -> {
                    String sn = input.readNonEmpty("Student Number (e.g. 2026-0001): ");
                    String name = input.readNonEmpty("Full Name: ");
                    String course = input.readNonEmpty("Course (e.g. BSIT): ");
                    int yr = input.readInt("Year Level (1-5): ", 1, 5);
                    view.showMessage(service.addStudentWithType(sn, name, course, yr, "REGULAR")
                            ? "Regular student enrolled."
                            : "Student number already exists.");
                }
                case "2" -> {
                    String sn = input.readNonEmpty("Transferee Student Number: ");
                    String name = input.readNonEmpty("Full Name: ");
                    String prevSchool = input.readNonEmpty("Previous School: ");
                    String course = input.readNonEmpty("Target Course (e.g. BSIT): ");
                    int yr = input.readInt("Credited Year Level (1-5): ", 1, 5);
                    boolean ok = service.addStudentWithType(sn, name + " (From: " + prevSchool + ")", course, yr, "TRANSFEREE");
                    view.showMessage(ok ? "Transferee student enrolled." : "Student number already exists.");
                }
                case "3" -> view.printStudents(service.getStudents());
                case "4" -> {
                    String sn = input.readNonEmpty("Student Number: ");
                    view.printSubjects(service.getSubjects());
                    String sc = input.readNonEmpty("Subject Code: ");
                    String sem = input.readNonEmpty("Semester (e.g. 1st Sem 2026-2027): ");
                    view.showMessage(service.enrollStudent(sn, sc, sem));
                }
                case "5" -> {
                    String sn = input.readNonEmpty("Student Number: ");
                    view.printEnrollments(service.getEnrollmentsByStudent(sn));
                }
                case "6" -> handleAddSubject();
                case "7" -> {
                    var reqs = service.getShiftingRequests();
                    view.printShiftingRequests(reqs);
                    if (!reqs.isEmpty()) {
                        String reqId = input.readLine("Enter Request ID to process (or Enter to skip): ");
                        if (!reqId.isEmpty()) {
                            boolean approve = input.readLine("Approve request? (y/n): ").equalsIgnoreCase("y");
                            view.showMessage(service.processShiftingRequest(reqId, approve)
                                    ? "Shifting request updated."
                                    : "Request ID not found.");
                        }
                    }
                }
                case "0" -> { return; }
                default -> view.showMessage("Invalid choice.");
            }
        }
    }

    private void handleCashier() {
        while (true) {
            view.showCashierMenu();
            String c = input.readLine("Choice: ");
            switch (c) {
                case "1" -> {
                    String sn = input.readNonEmpty("Student Number: ");
                    Student st = service.findStudent(sn);
                    if (st == null) {
                        view.showMessage("Student not found.");
                    } else {
                        view.printDetailedBilling(
                                st,
                                service.calculateTotalAssessment(sn),
                                service.getLabFee(sn),
                                service.getMiscFee(sn),
                                service.getWaivedAmount(sn),
                                service.calculateTotalPaid(sn),
                                service.calculateNetBalance(sn)
                        );
                    }
                }
                case "2" -> {
                    String sn = input.readNonEmpty("Student Number: ");
                    if (service.findStudent(sn) == null) {
                        view.showMessage("Student not found.");
                    } else {
                        double lab = input.readPositiveDouble("New Laboratory Fee: ");
                        double misc = input.readPositiveDouble("New Miscellaneous Fee: ");
                        service.setAdditionalFees(sn, lab, misc);
                        view.showMessage("Lab Fee and Miscellaneous Fee updated.");
                    }
                }
                case "3" -> {
                    String sn = input.readNonEmpty("Student Number: ");
                    if (service.findStudent(sn) == null) {
                        view.showMessage("Student not found.");
                    } else {
                        double waive = input.readPositiveDouble("Amount to Waive: ");
                        service.waiveFee(sn, waive);
                        view.showMessage("Fee waived successfully.");
                    }
                }
                case "4" -> {
                    String sn = input.readNonEmpty("Student Number: ");
                    double amt = input.readPositiveDouble("Payment Amount: ");
                    String method = input.readNonEmpty("Payment Method (CASH / GCASH / BANK): ");
                    view.showMessage(service.recordPayment(sn, amt, method));
                }
                case "5" -> {
                    String sn = input.readNonEmpty("Student Number: ");
                    view.printPayments(service.getPaymentsByStudent(sn));
                }
                case "0" -> { return; }
                default -> view.showMessage("Invalid choice.");
            }
        }
    }