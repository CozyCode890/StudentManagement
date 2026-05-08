package vn.edu.studentmanagement.app;

import java.util.Objects;

import vn.edu.studentmanagement.model.Student;
import vn.edu.studentmanagement.service.StudentService;
import vn.edu.studentmanagement.ui.console.ConsoleMessagePrinter;
import vn.edu.studentmanagement.ui.console.ConsolePrompt;

class ScheduleStudentSelector {
  private final StudentService studentService;

  ScheduleStudentSelector(StudentService studentService) {
    this.studentService = Objects.requireNonNull(studentService);
  }

  Student askForStudent() {
    String sid = ConsolePrompt.trimmed("Enter student ID: ");
    try {
      Student student = studentService.findById(sid);
      if (student == null) {
        ConsoleMessagePrinter.warning("Student not found with ID: " + sid);
      }
      return student;
    } catch (IllegalArgumentException | IllegalStateException e) {
      ConsoleMessagePrinter.error(e);
      return null;
    }
  }
}
