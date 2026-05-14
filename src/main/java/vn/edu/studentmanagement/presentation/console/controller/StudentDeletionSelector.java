package vn.edu.studentmanagement.presentation.console.controller;

import java.util.List;
import java.util.Objects;

import vn.edu.studentmanagement.application.student.StudentService;
import vn.edu.studentmanagement.domain.model.Student;
import vn.edu.studentmanagement.presentation.console.io.ConsoleMessagePrinter;
import vn.edu.studentmanagement.presentation.console.io.ConsolePause;
import vn.edu.studentmanagement.presentation.console.io.ConsolePrompt;
import vn.edu.studentmanagement.presentation.console.io.TerminalController;
import vn.edu.studentmanagement.presentation.console.renderer.StudentTableRenderer;

class StudentDeletionSelector {
  private static final int ROWS_PER_PAGE = 10;

  private final StudentService studentService;

  StudentDeletionSelector(StudentService studentService) {
    this.studentService = Objects.requireNonNull(studentService);
  }

  Student selectStudentToDelete() {
    while (true) {
      List<Student> candidates = searchCandidates();
      if (candidates == null) {
        return null;
      }

      SelectionResult selectionResult = selectFromCandidates(candidates);
      if (selectionResult.isBack()) {
        return null;
      }
      if (selectionResult.getStudent() != null) {
        return selectionResult.getStudent();
      }
    }
  }

  private List<Student> searchCandidates() {
    while (true) {
      try {
        if (studentService.findAll().isEmpty()) {
          throw new IllegalStateException("Student list is empty.");
        }

        TerminalController.clearScreen();
        String keyword = ConsolePrompt.trimmed(
            "\nEnter name keyword to delete (blank to show all, 0 to return): ");
        if (keyword.equals("0")) {
          return null;
        }

        List<Student> candidates = keyword.isEmpty()
            ? studentService.findAll()
            : studentService.findByName(keyword);

        if (candidates.isEmpty()) {
          ConsoleMessagePrinter.warning("No students matched your search.");
          ConsolePause.waitForEnter();
          continue;
        }

        return candidates;
      } catch (IllegalArgumentException | IllegalStateException e) {
        ConsoleMessagePrinter.error(e);
        ConsolePause.waitForEnter();
      }
    }
  }

  private SelectionResult selectFromCandidates(List<Student> candidates) {
    int totalPages = (int) Math.ceil((double) candidates.size() / ROWS_PER_PAGE);
    int currentPage = 0;
    String feedback = null;

    while (true) {
      TerminalController.clearScreen();
      int start = currentPage * ROWS_PER_PAGE;
      int end = Math.min(start + ROWS_PER_PAGE, candidates.size());

      if (feedback != null) {
        ConsoleMessagePrinter.warning(feedback);
        feedback = null;
      }

      System.out.println("\n--- Select Student To Delete: Page " + (currentPage + 1) + " of " + totalPages + " ---");
      StudentTableRenderer.render(candidates.subList(start, end), start + 1);

      System.out.println("\nEnter STT to delete | [N] Next | [P] Previous | [S] Search again | [B|0] Back");
      String choice = ConsolePrompt.upperTrimmed("Action: ");

      if (choice.equals("N") && currentPage < totalPages - 1) {
        currentPage++;
      } else if (choice.equals("P") && currentPage > 0) {
        currentPage--;
      } else if (choice.equals("S")) {
        return SelectionResult.searchAgain();
      } else if (choice.equals("B") || choice.equals("0")) {
        return SelectionResult.back();
      } else {
        Student selectedStudent = selectByNumber(candidates, choice);
        if (selectedStudent == null) {
          feedback = "Invalid STT or no more pages.";
        } else if (confirmDeletion(selectedStudent)) {
          return SelectionResult.selected(selectedStudent);
        } else {
          feedback = "Deletion canceled.";
        }
      }
    }
  }

  private Student selectByNumber(List<Student> candidates, String choice) {
    try {
      int selectedNumber = Integer.parseInt(choice);
      if (selectedNumber < 1 || selectedNumber > candidates.size()) {
        return null;
      }
      return candidates.get(selectedNumber - 1);
    } catch (NumberFormatException e) {
      return null;
    }
  }

  private boolean confirmDeletion(Student student) {
    System.out.println();
    StudentTableRenderer.render(List.of(student), 1);
    String confirm = ConsolePrompt.upperTrimmed("Confirm delete this student? (Y/N): ");
    return confirm.equals("Y");
  }

  private static class SelectionResult {
    private final Student student;
    private final boolean back;

    private SelectionResult(Student student, boolean back) {
      this.student = student;
      this.back = back;
    }

    static SelectionResult selected(Student student) {
      return new SelectionResult(Objects.requireNonNull(student), false);
    }

    static SelectionResult searchAgain() {
      return new SelectionResult(null, false);
    }

    static SelectionResult back() {
      return new SelectionResult(null, true);
    }

    Student getStudent() {
      return student;
    }

    boolean isBack() {
      return back;
    }
  }
}
