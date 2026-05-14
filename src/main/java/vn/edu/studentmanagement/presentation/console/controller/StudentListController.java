package vn.edu.studentmanagement.presentation.console.controller;

import java.util.List;
import java.util.Objects;

import vn.edu.studentmanagement.domain.model.Student;
import vn.edu.studentmanagement.application.student.StudentService;
import vn.edu.studentmanagement.presentation.console.io.ConsoleIO;
import vn.edu.studentmanagement.presentation.console.io.ConsoleMessagePrinter;
import vn.edu.studentmanagement.presentation.console.io.ConsolePaginator;
import vn.edu.studentmanagement.presentation.console.io.ConsolePause;
import vn.edu.studentmanagement.presentation.console.io.ConsolePrompt;
import vn.edu.studentmanagement.presentation.console.io.TerminalController;
import vn.edu.studentmanagement.presentation.console.menu.StudentMenuView;
import vn.edu.studentmanagement.presentation.console.renderer.StudentTableRenderer;

public class StudentListController {
  private static final int ROWS_PER_PAGE = 10;

  private final StudentService studentService;
  private final StudentMenuView menuView = new StudentMenuView();

  public StudentListController(StudentService studentService) {
    this.studentService = Objects.requireNonNull(studentService);
  }

  public void show() {
    while (true) {
      TerminalController.clearScreen();

      menuView.printViewOptions();

      String choice = ConsolePrompt.trimmed("Choice: ");
      if (choice.equals("0")) {
        break;
      }

      if (choice.equals("1")) {
        showAllStudents();
      } else if (choice.equals("2")) {
        searchByName();
      } else {
        TerminalController.clearScreen();
        ConsoleMessagePrinter.warning("Invalid choice.");
        ConsolePause.waitForEnter();
      }
    }
  }

  private void showAllStudents() {
    showPaginated(studentService.findAll(), "Student list is empty.");
  }

  private void searchByName() {
    String keyword = ConsoleIO.prompt("Enter name keyword: ");
    try {
      List<Student> students = studentService.findByName(keyword);
      showPaginated(students, "No students matched your search.");
    } catch (IllegalArgumentException e) {
      ConsoleMessagePrinter.error(e);
      ConsolePause.waitForEnter();
    }
  }

  public void showPaginated(List<Student> students, String emptyMessage) {
    ConsolePaginator.show(
        students,
        ROWS_PER_PAGE,
        emptyMessage,
        StudentTableRenderer::render);
  }
}
