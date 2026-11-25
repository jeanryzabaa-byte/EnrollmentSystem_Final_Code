public void showRegistrarMenu() {
        System.out.println("\n--- REGISTRAR MENU ---");
        System.out.println("1. Enroll New / Regular Student");
        System.out.println("2. Enroll Transferee Student");
        System.out.println("3. View All Students");
        System.out.println("4. Enroll Student in Subject");
        System.out.println("5. View Student Enrollments");
        System.out.println("6. Add Subject");
        System.out.println("7. View & Process Shifting Requests");
        System.out.println("0. Logout");
    }

    public void showCashierMenu() {
        System.out.println("\n--- CASHIERING MENU ---");
        System.out.println("1. View Full Assessment (Tuition + Lab + Misc - Waived)");
        System.out.println("2. Set / Update Lab Fee & Miscellaneous Fee");
        System.out.println("3. Waive Fee / Discount");
        System.out.println("4. Record Payment");
        System.out.println("5. View Student Payment History");
        System.out.println("0. Logout");
    }

    public void printDetailedBilling(Student student, double tuition, double lab, double misc, double waived, double paid, double balance) {
        System.out.println("----------------------------------------------------");
        System.out.printf("Student: %s (%s) [%s]%n", student.getFullName(), student.getStudentNumber(), student.getStudentType());
        System.out.printf("  Tuition Fee       : %.2f%n", tuition);
        System.out.printf("  Laboratory Fee    : %.2f%n", lab);
        System.out.printf("  Miscellaneous Fee : %.2f%n", misc);
        System.out.printf("  Waived / Discount : -%.2f%n", waived);
        System.out.printf("  Total Assessment  : %.2f%n", Math.max(0, tuition + lab + misc - waived));
        System.out.printf("  Total Paid        : %.2f%n", paid);
        System.out.printf("  Remaining Balance : %.2f%n", balance);
        System.out.println("----------------------------------------------------");
    }