package vn.edu.studentmanagement.presentation.console.controller;

import java.util.Objects;

import vn.edu.studentmanagement.domain.model.Student;
import vn.edu.studentmanagement.application.student.StudentService;
import vn.edu.studentmanagement.presentation.console.io.ConsoleMessagePrinter;
import vn.edu.studentmanagement.presentation.console.io.ConsolePrompt;

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
