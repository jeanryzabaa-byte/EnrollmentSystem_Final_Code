package enrollmentsystem.model.subject;

public class Subject {
    private final String subjectCode;
    private final String title;
    private final int units;
    private final double feePerUnit;
    private String instructorUsername;

    public Subject(String subjectCode, String title, int units, double feePerUnit, String instructorUsername) {
        this.subjectCode = subjectCode;
        this.title = title;
        this.units = units;
        this.feePerUnit = feePerUnit;
        this.instructorUsername = instructorUsername;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public String getTitle() {
        return title;
    }

    public int getUnits() {
        return units;
    }

    public double getFeePerUnit() {
        return feePerUnit;
    }

    public double getTotalTuition() {
        return units * feePerUnit;
    }

    public String getInstructorUsername() {
        return instructorUsername;
    }

    public void setInstructorUsername(String instructorUsername) {
        this.instructorUsername = instructorUsername;
    }

    public String toDataString() {
        return escape(subjectCode) + "|" + escape(title) + "|" + units + "|" + feePerUnit + "|" + escape(instructorUsername);
    }

    public static Subject fromDataString(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 5) return null;
        try {
            int u = Integer.parseInt(parts[2].trim());
            double fee = Double.parseDouble(parts[3].trim());
            return new Subject(unescape(parts[0]), unescape(parts[1]), u, fee, unescape(parts[4]));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%d units, %.2f/unit, Total: %.2f) [Instructor: %s]",
                subjectCode, title, units, feePerUnit, getTotalTuition(),
                (instructorUsername == null || instructorUsername.isBlank()) ? "Unassigned" : instructorUsername);
    }

    private static String escape(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("|", "\\p");
    }

    private static String unescape(String s) {
        return s == null ? "" : s.replace("\\p", "|").replace("\\\\", "\\");
    }
}