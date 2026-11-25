import controller.EnrollmentController;
import java.util.Scanner;
import service.EnrollmentService;
import view.ConsoleView;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            EnrollmentService service = new EnrollmentService();
            ConsoleView view = new ConsoleView();
            EnrollmentController controller = new EnrollmentController(service, view);

            boolean running = true;
            while (running) {
                System.out.println("\n========== REGISTRAR MODULE ==========");
                System.out.println("1. View All Students");
                System.out.println("2. View Student Enrollments");
                System.out.println("0. Exit");
                System.out.println("======================================");
                System.out.print("Choice: ");

                String input = scanner.nextLine().trim();
                switch (input) {
                    case "1":
                        controller.viewAllStudents();
                        controller.pause();
                        break;
                    case "2":
                        controller.viewStudentEnrollments();
                        controller.pause();
                        break;
                    case "0":
                        running = false;
                        view.showMessage("Exiting...");
                        break;
                    default:
                        view.showMessage("Invalid choice.");
                        break;
                }
            }
        }
    }
}