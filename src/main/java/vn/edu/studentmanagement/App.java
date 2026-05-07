package vn.edu.studentmanagement;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import vn.edu.studentmanagement.storage.CourseCatalog;
import vn.edu.studentmanagement.storage.CsvScheduleRepository;
import vn.edu.studentmanagement.storage.CsvStudentRepository;
import vn.edu.studentmanagement.service.ScheduleService;
import vn.edu.studentmanagement.service.StudentService;
import vn.edu.studentmanagement.ui.ConsoleIO;
import vn.edu.studentmanagement.ui.MainMenu;
import vn.edu.studentmanagement.ui.ScheduleMenu;
import vn.edu.studentmanagement.ui.StudentMenu;

public class App {
  private static final Scanner SC = new Scanner(System.in, StandardCharsets.UTF_8);

  public static void main(String[] args) {
    CsvStudentRepository studentRepository = new CsvStudentRepository();
    CourseCatalog courseCatalog = new CourseCatalog();
    CsvScheduleRepository scheduleRepository = new CsvScheduleRepository(courseCatalog);
    studentRepository.ensureFileExists();
    scheduleRepository.ensureFileExists();

    StudentService studentService = new StudentService(studentRepository);
    ScheduleService scheduleService = new ScheduleService(studentService, courseCatalog, scheduleRepository);

    while (true) {
      ConsoleIO.clearScreen();
      MainMenu.printMenu();
      String choice = SC.nextLine().trim();

      if (choice.equalsIgnoreCase("q") || choice.equals("0")) {
        studentService.flushPendingChanges();
        scheduleService.flushPendingChanges();
        System.out.println("Bye 👋");
        break;
      }

      switch (choice) {
        case "1" -> StudentMenu.run(studentService, scheduleService);
        case "2" -> ScheduleMenu.run(studentService, courseCatalog, scheduleService);
        default -> {
          System.out.println("Invalid choice. Please select 1, 2, 0, or q.\n");
          System.out.print("Press Enter to continue...");
          SC.nextLine();
        }
      }
    }
  }
}
