import controller.EnrollmentController;
import service.EnrollmentService;
import util.InputValidator;
import view.ConsoleView;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            EnrollmentService service = new EnrollmentService();
            ConsoleView view = new ConsoleView();
            InputValidator input = new InputValidator(scanner);
            EnrollmentController controller = new EnrollmentController(service, view, input);
            controller.run();
        }
    }
}
