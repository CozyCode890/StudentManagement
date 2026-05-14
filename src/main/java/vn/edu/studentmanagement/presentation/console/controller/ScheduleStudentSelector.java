package vn.edu.studentmanagement.presentation.console.controller;

import java.util.Objects;

import vn.edu.studentmanagement.domain.model.Student;
import vn.edu.studentmanagement.application.student.StudentService;
import vn.edu.studentmanagement.presentation.console.io.ConsoleMessagePrinter;
import vn.edu.studentmanagement.presentation.console.io.ConsolePause;
import vn.edu.studentmanagement.presentation.console.io.ConsolePrompt;
import vn.edu.studentmanagement.domain.validation.StudentValidator;

class ScheduleStudentSelector {
  private final StudentService studentService;
  private final StudentValidator studentValidator;

  ScheduleStudentSelector(StudentService studentService) {
    this.studentService = Objects.requireNonNull(studentService);
    this.studentValidator = new StudentValidator();
  }

  Student askForStudent() {
    while (true) {
      String sid = ConsolePrompt.trimmed("Enter student ID (0 to return): ");
      if (sid.equals("0")) {
        return null;
      }

      try {
        studentValidator.validateStudentIdFormat(sid);
        Student student = studentService.findById(sid);
        if (student == null) {
          ConsoleMessagePrinter.warning("Student not found with ID: " + sid);
          ConsolePause.waitForEnter();
          continue;
        }
        return student;
      } catch (IllegalArgumentException | IllegalStateException e) {
        ConsoleMessagePrinter.error(e);
        ConsolePause.waitForEnter();
      }
    }
  }
}
