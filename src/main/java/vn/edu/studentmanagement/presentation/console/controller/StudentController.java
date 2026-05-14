package vn.edu.studentmanagement.presentation.console.controller;

import java.util.Objects;

import vn.edu.studentmanagement.domain.model.Student;
import vn.edu.studentmanagement.application.schedule.ScheduleService;
import vn.edu.studentmanagement.application.student.StudentService;
import vn.edu.studentmanagement.presentation.console.io.ConsoleIO;
import vn.edu.studentmanagement.presentation.console.io.ConsoleMessagePrinter;
import vn.edu.studentmanagement.presentation.console.io.ConsolePause;
import vn.edu.studentmanagement.presentation.console.io.ConsolePrompt;
import vn.edu.studentmanagement.presentation.console.io.TerminalController;
import vn.edu.studentmanagement.presentation.console.menu.StudentMenuView;
import vn.edu.studentmanagement.presentation.console.view.StudentView;

public class StudentController {
  private static StudentService studentService;
  private static ScheduleService scheduleService;
  private static StudentListController studentListController;
  private static final StudentMenuView MENU_VIEW = new StudentMenuView();
  private static final StudentView STUDENT_VIEW = new StudentView();

  public static void run(StudentService sharedStudentService, ScheduleService sharedScheduleService) {
    studentService = Objects.requireNonNull(sharedStudentService);
    scheduleService = Objects.requireNonNull(sharedScheduleService);
    studentListController = new StudentListController(studentService);
    while (true) {
      TerminalController.clearScreen();

      MENU_VIEW.printMenu();

      String choice = ConsolePrompt.trimmed("Choose: ");
      switch (choice) {
        case "1" -> studentListController.show();
        case "2" -> {
          addStudent();
          ConsolePause.waitForEnter();
        }
        case "3" -> {
          deleteStudentById();
          ConsolePause.waitForEnter();
        }
        case "0" -> {
          if (flushPendingStudentChanges()) {
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

  public static void addStudent() {
    String id = ConsoleIO.prompt("\nEnter ID: ");
    String name = ConsoleIO.prompt("\nEnter name: ");
    String gender = ConsoleIO.prompt("Enter gender (Male/Female/M/F): ");

    try {
      Student student = studentService.addStudent(id, name, gender);
      STUDENT_VIEW.printAdded(student);
    } catch (IllegalArgumentException | IllegalStateException e) {
      ConsoleMessagePrinter.error(e);
    }
  }

  public static void deleteStudentById() {
    String input = ConsolePrompt.trimmed("\nEnter ID to delete: ");

    try {
      Student deletedStudent = studentService.deleteStudentById(input);
      boolean removedSchedule = scheduleService.removeScheduleByStudentId(input);
      STUDENT_VIEW.printDeleted(deletedStudent);
      if (removedSchedule) {
        STUDENT_VIEW.printScheduleRemoved(input);
      }
    } catch (IllegalArgumentException | IllegalStateException e) {
      ConsoleMessagePrinter.error(e);
    }
  }

  private static boolean flushPendingStudentChanges() {
    try {
      studentService.flushPendingChanges();
      scheduleService.flushPendingChanges();
      return true;
    } catch (IllegalStateException e) {
      ConsoleMessagePrinter.error(e);
      ConsolePause.waitForEnter();
      return false;
    }
  }
}
