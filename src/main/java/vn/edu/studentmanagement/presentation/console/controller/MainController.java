package vn.edu.studentmanagement.presentation.console.controller;

import java.util.Objects;

import vn.edu.studentmanagement.application.StudentManagementService;
import vn.edu.studentmanagement.application.schedule.ScheduleService;
import vn.edu.studentmanagement.application.student.StudentService;
import vn.edu.studentmanagement.presentation.console.io.ConsoleIO;
import vn.edu.studentmanagement.presentation.console.io.ConsoleMessagePrinter;
import vn.edu.studentmanagement.presentation.console.io.ConsolePause;
import vn.edu.studentmanagement.presentation.console.io.TerminalController;
import vn.edu.studentmanagement.presentation.console.menu.MainMenuView;

public class MainController {
  private final StudentManagementService studentManagementService;
  private final StudentController studentController;
  private final ScheduleController scheduleController;

  public MainController(
      StudentService studentService,
      ScheduleService scheduleService,
      StudentManagementService studentManagementService) {
    Objects.requireNonNull(studentService);
    Objects.requireNonNull(scheduleService);
    this.studentManagementService = Objects.requireNonNull(studentManagementService);

    this.studentController = new StudentController(studentService, studentManagementService);
    this.scheduleController = new ScheduleController(studentService, scheduleService);
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
        case "1" -> studentController.run();
        case "2" -> scheduleController.run();
        default -> {
          ConsoleMessagePrinter.warning("Invalid choice. Please select 1, 2, 0, or q.\n");
          ConsolePause.waitForEnter();
        }
      }
    }
  }

  private boolean flushPendingChanges() {
    try {
      studentManagementService.flushPendingChanges();
      return true;
    } catch (IllegalStateException e) {
      ConsoleMessagePrinter.error(e);
      ConsolePause.waitForEnter();
      return false;
    }
  }
}
