package vn.edu.studentmanagement.presentation.console.controller;

import java.util.Objects;

import vn.edu.studentmanagement.application.StudentManagementService;
import vn.edu.studentmanagement.application.student.StudentService;
import vn.edu.studentmanagement.domain.model.Student;
import vn.edu.studentmanagement.presentation.console.io.ConsoleMessagePrinter;
import vn.edu.studentmanagement.presentation.console.io.ConsolePause;
import vn.edu.studentmanagement.presentation.console.io.ConsolePrompt;
import vn.edu.studentmanagement.presentation.console.io.TerminalController;
import vn.edu.studentmanagement.presentation.console.menu.StudentMenuView;
import vn.edu.studentmanagement.presentation.console.view.StudentView;

public class StudentController {
  private final StudentService studentService;
  private final StudentListController studentListController;
  private final StudentDeletionSelector studentDeletionSelector;
  private final StudentManagementService studentManagementService;
  private final StudentMenuView menuView = new StudentMenuView();
  private final StudentView studentView = new StudentView();

  public StudentController(StudentService studentService, StudentManagementService studentManagementService) {
    this.studentService = Objects.requireNonNull(studentService);
    this.studentManagementService = Objects.requireNonNull(studentManagementService);
    this.studentListController = new StudentListController(studentService);
    this.studentDeletionSelector = new StudentDeletionSelector(studentService);
  }

  public void run() {
    while (true) {
      TerminalController.clearScreen();

      menuView.printMenu();

      String choice = ConsolePrompt.trimmed("Choose: ");
      switch (choice) {
        case "1" -> studentListController.show();
        case "2" -> {
          if (addStudent()) {
            ConsolePause.waitForEnter();
          }
        }
        case "3" -> {
          if (deleteStudent()) {
            ConsolePause.waitForEnter();
          }
        }
        case "0" -> {
          if (flushPendingStudentChanges()) {
            return;
          }
        }
        default -> {
          ConsoleMessagePrinter.warning("Invalid choice. Please continue.");
          ConsolePause.waitForEnter();
        }
      }
    }
  }

  private boolean addStudent() {
    try {
      String id = ConsolePrompt.trimmed("\nEnter ID (0 to return): ");
      if (id.equals("0")) {
        return false;
      }

      String name = ConsolePrompt.trimmed("\nEnter name (0 to return): ");
      if (name.equals("0")) {
        return false;
      }

      String gender = ConsolePrompt.trimmed("Enter gender (Male/Female/M/F, 0 to return): ");
      if (gender.equals("0")) {
        return false;
      }

      Student student = studentService.addStudent(id, name, gender);
      studentView.printAdded(student);
      return true;
    } catch (IllegalArgumentException | IllegalStateException e) {
      ConsoleMessagePrinter.error(e);
      return true;
    }
  }

  private boolean deleteStudent() {
    Student student = studentDeletionSelector.selectStudentToDelete();
    if (student == null) {
      return false;
    }

    try {
      StudentManagementService.DeleteStudentResult result = studentManagementService.deleteStudentById(student.getId());

      studentView.printDeleted(result.getStudent());
      if (result.isScheduleRemoved()) {
        studentView.printScheduleRemoved(result.getStudent().getId());
      }
      return true;
    } catch (IllegalArgumentException | IllegalStateException e) {
      ConsoleMessagePrinter.error(e);
      return true;
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
