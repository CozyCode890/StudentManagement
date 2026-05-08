package vn.edu.studentmanagement.presentation.console.controller;

import java.util.Objects;

import vn.edu.studentmanagement.application.schedule.ScheduleService;
import vn.edu.studentmanagement.application.student.StudentService;
import vn.edu.studentmanagement.domain.catalog.CourseCatalog;
import vn.edu.studentmanagement.presentation.console.io.ConsoleIO;
import vn.edu.studentmanagement.presentation.console.io.ConsoleMessagePrinter;
import vn.edu.studentmanagement.presentation.console.io.TerminalController;
import vn.edu.studentmanagement.presentation.console.menu.MainMenuView;

public class MainController {
  private final StudentService studentService;
  private final ScheduleService scheduleService;
  private final CourseCatalog courseCatalog;

  public MainController(
      StudentService studentService,
      ScheduleService scheduleService,
      CourseCatalog courseCatalog) {
    this.studentService = Objects.requireNonNull(studentService);
    this.scheduleService = Objects.requireNonNull(scheduleService);
    this.courseCatalog = Objects.requireNonNull(courseCatalog);
  }

  public void run() {
    while (true) {
      TerminalController.clearScreen();
      MainMenuView.printMenu();
      String choice = ConsoleIO.readLine().trim();

      if (choice.equalsIgnoreCase("q") || choice.equals("0")) {
        if (flushPendingChanges()) {
          ConsoleIO.println("Bye 👋");
          return;
        }
        continue;
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

  private boolean flushPendingChanges() {
    try {
      studentService.flushPendingChanges();
      scheduleService.flushPendingChanges();
      return true;
    } catch (IllegalStateException e) {
      ConsoleMessagePrinter.error(e);
      ConsoleIO.print("Press Enter to continue...");
      ConsoleIO.readLine();
      return false;
    }
  }
}
