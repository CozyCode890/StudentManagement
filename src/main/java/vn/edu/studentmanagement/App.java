package vn.edu.studentmanagement;

import vn.edu.studentmanagement.storage.CourseCatalog;
import vn.edu.studentmanagement.storage.CsvScheduleRepository;
import vn.edu.studentmanagement.storage.CsvStudentRepository;
import vn.edu.studentmanagement.service.ScheduleService;
import vn.edu.studentmanagement.service.StudentService;
import vn.edu.studentmanagement.app.ScheduleController;
import vn.edu.studentmanagement.app.StudentController;
import vn.edu.studentmanagement.ui.console.ConsoleIO;
import vn.edu.studentmanagement.ui.console.ConsoleMessagePrinter;
import vn.edu.studentmanagement.ui.console.TerminalController;
import vn.edu.studentmanagement.ui.menu.MainMenuView;

public class App {
  public static void main(String[] args) {
    CsvStudentRepository studentRepository = new CsvStudentRepository();
    CourseCatalog courseCatalog = new CourseCatalog();
    CsvScheduleRepository scheduleRepository = new CsvScheduleRepository(courseCatalog);
    studentRepository.ensureFileExists();
    scheduleRepository.ensureFileExists();

    StudentService studentService = new StudentService(studentRepository);
    ScheduleService scheduleService = new ScheduleService(studentService, courseCatalog, scheduleRepository);

    while (true) {
      TerminalController.clearScreen();
      MainMenuView.printMenu();
      String choice = ConsoleIO.readLine().trim();

      if (choice.equalsIgnoreCase("q") || choice.equals("0")) {
        try {
          studentService.flushPendingChanges();
          scheduleService.flushPendingChanges();
          ConsoleIO.println("Bye 👋");
          break;
        } catch (IllegalStateException e) {
          ConsoleMessagePrinter.error(e);
          ConsoleIO.print("Press Enter to continue...");
          ConsoleIO.readLine();
        }
      }

      switch (choice) {
        case "1" -> StudentController.run(studentService, scheduleService);
        case "2" -> ScheduleController.run(studentService, courseCatalog, scheduleService);
        default -> {
          ConsoleMessagePrinter.warning("Invalid choice. Please select 1, 2, 0, or q.\n");
          ConsoleIO.print("Press Enter to continue...");
          ConsoleIO.readLine();
        }
      }
    }
  }
}
