package model;

public class Subject {
    private final String code;
    private final String title;
    private final int units;
    private final double feePerUnit;
    private final String instructor;

    public Subject(String code, String title, int units, double feePerUnit, String instructor) {
        this.code = code;
        this.title = title;
        this.units = units;
        this.feePerUnit = feePerUnit;
        this.instructor = instructor;
    }

    public String getCode() { return code; }
    public String getTitle() { return title; }
    public int getUnits() { return units; }
    public double getFeePerUnit() { return feePerUnit; }
    public String getInstructor() { return instructor; }

    public String toDataString() {
        return escape(code) + "|" + escape(title) + "|" + units + "|" + feePerUnit + "|" + escape(instructor);
    }

    public static Subject fromDataString(String line) {
        if (line == null || line.isBlank()) return null;
        String[] parts = line.split("\\|", -1);
        if (parts.length < 5) return null;
        try {
            return new Subject(
                    unescape(parts[0]),
                    unescape(parts[1]),
                    Integer.parseInt(parts[2].trim()),
                    Double.parseDouble(parts[3].trim()),
                    unescape(parts[4])
            );
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%d units @ %.2f/unit) | Instructor: %s",
                code, title, units, feePerUnit, instructor);
    }

    private static String escape(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("|", "\\p");
    }

    private static String unescape(String s) {
        return s == null ? "" : s.replace("\\p", "|").replace("\\\\", "\\");
    }
}


