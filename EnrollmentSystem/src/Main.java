import java.util.Scanner;

import view.EnrollmentMenu;
import controller.EnrollmentController;
import service.EnrollmentService;
import repository.StudentRepository;
import repository.FileStudentRepository;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            StudentRepository repository = new FileStudentRepository();
            EnrollmentService service = new EnrollmentService(repository);
            EnrollmentMenu menu = new EnrollmentMenu(scanner);
            EnrollmentController controller =
                    new EnrollmentController(menu, service);

            controller.start();
        } catch (Exception e) {
            System.out.println("Application error: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}
