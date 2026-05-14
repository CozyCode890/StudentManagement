package vn.edu.studentmanagement.presentation.console.controller;

import java.util.Objects;

import vn.edu.studentmanagement.application.schedule.ScheduleService;
import vn.edu.studentmanagement.application.student.StudentService;
import vn.edu.studentmanagement.presentation.console.io.ConsoleMessagePrinter;
import vn.edu.studentmanagement.presentation.console.io.ConsolePause;
import vn.edu.studentmanagement.presentation.console.io.ConsolePrompt;
import vn.edu.studentmanagement.presentation.console.io.TerminalController;
import vn.edu.studentmanagement.presentation.console.menu.ScheduleMenuView;
import vn.edu.studentmanagement.presentation.console.view.ScheduleView;

public class ScheduleController {
  private final ScheduleService scheduleService;
  private final ScheduleView scheduleView;
  private final ScheduleStudentSelector studentSelector;
  private final ScheduleViewer scheduleViewer;
  private final ScheduleCourseAdder courseAdder;
  private final ScheduleCourseRemover courseRemover;
  private final ScheduleMenuView menuView;

  public ScheduleController(StudentService sharedStudentService, ScheduleService sharedScheduleService) {
    StudentService studentService = Objects.requireNonNull(sharedStudentService);
    this.scheduleService = Objects.requireNonNull(sharedScheduleService);

    this.scheduleView = new ScheduleView();
    this.studentSelector = new ScheduleStudentSelector(studentService);
    this.scheduleViewer = new ScheduleViewer(scheduleService, studentSelector, scheduleView);
    this.courseAdder = new ScheduleCourseAdder(scheduleService,
        studentSelector,
        scheduleView);
    this.courseRemover = new ScheduleCourseRemover(scheduleService, studentSelector);
    this.menuView = new ScheduleMenuView();
  }

  public void run() {
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
          if (flushPendingChanges()) {
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

  private boolean flushPendingChanges() {
    try {
      scheduleService.flushPendingChanges();
      return true;
    } catch (IllegalStateException e) {
      ConsoleMessagePrinter.error(e);
      ConsolePause.waitForEnter();
      return false;
    }
  }
}
