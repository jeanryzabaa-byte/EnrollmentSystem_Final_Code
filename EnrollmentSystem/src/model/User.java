package enrollmentsystem.model.user;

public class User {
    private final String username;
    private final String password;
    private final String role; // ADMIN, REGISTRAR, CASHIER, STUDENT, INSTRUCTOR
    private final String linkedId;

    public User(String username, String password, String role, String linkedId) {
        this.username = username;
        this.password = password;
        this.role = role.toUpperCase();
        this.linkedId = linkedId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public String getLinkedId() {
        return linkedId;
    }

    public boolean authenticate(String password) {
        return this.password.equals(password);
    }

    public String toDataString() {
        return escape(username) + "|" + escape(password) + "|" + escape(role) + "|" + escape(linkedId);
    }

    public static User fromDataString(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 4) return null;
        return new User(unescape(parts[0]), unescape(parts[1]), unescape(parts[2]), unescape(parts[3]));
    }

    private static String escape(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("|", "\\p");
    }

    private static String unescape(String s) {
        return s == null ? "" : s.replace("\\p", "|").replace("\\\\", "\\");
    }
}