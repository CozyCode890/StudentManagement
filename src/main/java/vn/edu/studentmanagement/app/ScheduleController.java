package vn.edu.studentmanagement.app;

import java.util.Objects;

import vn.edu.studentmanagement.service.ScheduleService;
import vn.edu.studentmanagement.service.StudentService;
import vn.edu.studentmanagement.storage.CourseCatalog;
import vn.edu.studentmanagement.ui.console.ConsoleMessagePrinter;
import vn.edu.studentmanagement.ui.console.ConsolePause;
import vn.edu.studentmanagement.ui.console.ConsolePrompt;
import vn.edu.studentmanagement.ui.console.TerminalController;
import vn.edu.studentmanagement.ui.menu.ScheduleMenuView;
import vn.edu.studentmanagement.ui.view.ScheduleView;

public class ScheduleController {
  public static void run(
      StudentService sharedStudentService,
      CourseCatalog sharedCourseCatalog,
      ScheduleService sharedScheduleService) {
    StudentService studentService = Objects.requireNonNull(sharedStudentService);
    CourseCatalog courseCatalog = Objects.requireNonNull(sharedCourseCatalog);
    ScheduleService scheduleService = Objects.requireNonNull(sharedScheduleService);
    ScheduleView scheduleView = new ScheduleView();

    ScheduleStudentSelector studentSelector = new ScheduleStudentSelector(studentService);
    ScheduleViewer scheduleViewer = new ScheduleViewer(scheduleService, studentSelector, scheduleView);
    ScheduleCourseAdder courseAdder = new ScheduleCourseAdder(scheduleService, courseCatalog, studentSelector, scheduleView);
    ScheduleCourseRemover courseRemover = new ScheduleCourseRemover(scheduleService, studentSelector);
    ScheduleChangeFlusher changeFlusher = new ScheduleChangeFlusher(scheduleService);
    ScheduleMenuView menuView = new ScheduleMenuView();

    while (true) {
      TerminalController.clearScreen();

      menuView.printMenu();

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
