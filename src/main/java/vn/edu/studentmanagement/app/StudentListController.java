package vn.edu.studentmanagement.app;

import java.util.List;
import java.util.Objects;

import vn.edu.studentmanagement.model.Student;
import vn.edu.studentmanagement.service.StudentService;
import vn.edu.studentmanagement.ui.console.ConsoleIO;
import vn.edu.studentmanagement.ui.console.ConsoleMessagePrinter;
import vn.edu.studentmanagement.ui.console.ConsolePaginator;
import vn.edu.studentmanagement.ui.console.ConsolePause;
import vn.edu.studentmanagement.ui.console.ConsolePrompt;
import vn.edu.studentmanagement.ui.console.TerminalController;
import vn.edu.studentmanagement.ui.menu.StudentMenuView;
import vn.edu.studentmanagement.ui.renderer.StudentTableRenderer;

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
      studentService.validateStudentName(keyword);
    } catch (IllegalArgumentException e) {
      ConsoleMessagePrinter.error(e);
      ConsolePause.waitForEnter();
      return;
    }

    List<Student> students = studentService.findByName(keyword);
    showPaginated(students, "No students matched your search.");
  }

  public void showPaginated(List<Student> students, String emptyMessage) {
    ConsolePaginator.show(
        students,
        ROWS_PER_PAGE,
        emptyMessage,
        StudentTableRenderer::render);
  }
}
