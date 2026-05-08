package vn.edu.studentmanagement.ui;

import java.util.Objects;

import vn.edu.studentmanagement.service.ScheduleService;
import vn.edu.studentmanagement.service.StudentService;
import vn.edu.studentmanagement.storage.CourseCatalog;

public class ScheduleMenu {
  public static void run(
      StudentService sharedStudentService,
      CourseCatalog sharedCourseCatalog,
      ScheduleService sharedScheduleService) {
    StudentService studentService = Objects.requireNonNull(sharedStudentService);
    CourseCatalog courseCatalog = Objects.requireNonNull(sharedCourseCatalog);
    ScheduleService scheduleService = Objects.requireNonNull(sharedScheduleService);

    ScheduleStudentSelector studentSelector = new ScheduleStudentSelector(studentService);
    ScheduleViewer scheduleViewer = new ScheduleViewer(scheduleService, studentSelector);
    ScheduleCourseCatalogView catalogView = new ScheduleCourseCatalogView(courseCatalog);
    ScheduleCourseAdder courseAdder = new ScheduleCourseAdder(scheduleService, studentSelector, catalogView);
    ScheduleCourseRemover courseRemover = new ScheduleCourseRemover(scheduleService, studentSelector);
    ScheduleChangeFlusher changeFlusher = new ScheduleChangeFlusher(scheduleService);

    while (true) {
      TerminalController.clearScreen();

      System.out.println("\n==================================");
      System.out.println("       COURSE REGISTRATION        ");
      System.out.println("==================================");
      System.out.println("1) View Student Schedule");
      System.out.println("2) Add Course to Schedule");
      System.out.println("3) Remove Course from Schedule");
      System.out.println("0) Back to main menu");

      String choice = ConsolePrompt.trimmed("Choose: ");
      switch (choice) {
        case "1" -> {
          scheduleViewer.show();
          ConsolePause.waitForEnter();
        }
        case "2" -> {
          courseAdder.show();
        }
        case "3" -> {
          courseRemover.show();
          ConsolePause.waitForEnter();
        }
        case "0" -> {
          if (changeFlusher.flush()) {
            return;
          }
        }
        default -> {
          ConsoleMessagePrinter.warning("Invalid choice.");
          ConsolePause.waitForEnter();
        }
      }
    }
  }
}
