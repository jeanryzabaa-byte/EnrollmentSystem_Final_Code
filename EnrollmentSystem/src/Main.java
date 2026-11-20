import controller.EnrollmentController;
import java.util.Scanner;
import repository.FileStudentRepository;
import repository.StudentRepository;
import service.EnrollmentService;
import view.EnrollmentMenu;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            StudentRepository repository = new FileStudentRepository();
            EnrollmentService service = new EnrollmentService(repository);
            EnrollmentMenu menu = new EnrollmentMenu(scanner);
            EnrollmentController controller = new EnrollmentController(menu, service);

            controller.start();
        } catch (Exception e) {
            System.out.println("Application error: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}