package vn.edu.studentmanagement.presentation.console.controller;

import java.util.Objects;

import vn.edu.studentmanagement.application.StudentManagementService;
import vn.edu.studentmanagement.application.student.StudentService;
import vn.edu.studentmanagement.domain.model.Student;
import vn.edu.studentmanagement.presentation.console.io.ConsoleIO;
import vn.edu.studentmanagement.presentation.console.io.ConsoleMessagePrinter;
import vn.edu.studentmanagement.presentation.console.io.ConsolePause;
import vn.edu.studentmanagement.presentation.console.io.ConsolePrompt;
import vn.edu.studentmanagement.presentation.console.io.TerminalController;
import vn.edu.studentmanagement.presentation.console.menu.StudentMenuView;
import vn.edu.studentmanagement.presentation.console.view.StudentView;

public class StudentController {
  private StudentService studentService;
  private StudentListController studentListController;
  private final StudentManagementService studentManagementService;
  private final StudentMenuView MENU_VIEW = new StudentMenuView();
  private final StudentView STUDENT_VIEW = new StudentView();

  public StudentController(StudentService studentService, StudentManagementService studentManagementService) {
    this.studentService = Objects.requireNonNull(studentService);
    this.studentManagementService = Objects.requireNonNull(studentManagementService);
    this.studentListController = new StudentListController(studentService);
  }

  public void run() {
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

  public void addStudent() {
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

  private void deleteStudentById() {
    String input = ConsolePrompt.trimmed("\nEnter ID to delete: ");

    try {
      StudentManagementService.DeleteStudentResult result = studentManagementService.deleteStudentById(input);

      STUDENT_VIEW.printDeleted(result.getStudent());
      if (result.isScheduleRemoved()) {
        STUDENT_VIEW.printScheduleRemoved(result.getStudent().getId());
      }
    } catch (IllegalArgumentException | IllegalStateException e) {
      ConsoleMessagePrinter.error(e);
    }
  }

  private boolean flushPendingStudentChanges() {
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
