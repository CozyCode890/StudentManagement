package vn.edu.studentmanagement.ui;

import java.util.List;
import java.util.Objects;

import vn.edu.studentmanagement.model.Student;
import vn.edu.studentmanagement.service.StudentService;

public class StudentListView {
  private static final int ROWS_PER_PAGE = 10;

  private final StudentService studentService;

  public StudentListView(StudentService studentService) {
    this.studentService = Objects.requireNonNull(studentService);
  }

  public void show() {
    while (true) {
      TerminalController.clearScreen();

      System.out.println("\n--- VIEW OPTIONS ---");
      System.out.println("1) Show All Students");
      System.out.println("2) Search by Name");
      System.out.println("0) Return");

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

    String lowerKeyword = keyword.toLowerCase();
    List<Student> students = studentService.findAll().stream()
        .filter(student -> student.getFullName().toLowerCase().contains(lowerKeyword))
        .toList();
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
