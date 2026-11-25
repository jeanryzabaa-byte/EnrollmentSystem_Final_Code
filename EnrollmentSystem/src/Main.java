import controller.EnrollmentController;
import repository.FileStudentRepository;
import repository.StudentRepository;
import service.EnrollmentService;
import view.EnrollmentMenu;

public class Main {
    public static void main(String[] args) {
        StudentRepository repository = new FileStudentRepository();
        EnrollmentService service = new EnrollmentService(repository);
        EnrollmentMenu menu = new EnrollmentMenu();
        EnrollmentController controller = new EnrollmentController(menu, service);

        controller.start();
    }
}