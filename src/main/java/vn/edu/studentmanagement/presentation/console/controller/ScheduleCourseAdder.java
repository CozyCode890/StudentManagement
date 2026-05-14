package vn.edu.studentmanagement.presentation.console.controller;

import java.util.Objects;

import vn.edu.studentmanagement.domain.model.Student;
import vn.edu.studentmanagement.application.schedule.ScheduleService;
import vn.edu.studentmanagement.presentation.console.io.ConsoleMessagePrinter;
import vn.edu.studentmanagement.presentation.console.io.ConsolePause;
import vn.edu.studentmanagement.presentation.console.io.ConsolePrompt;
import vn.edu.studentmanagement.presentation.console.view.ScheduleView;

class ScheduleCourseAdder {
  private final ScheduleService scheduleService;
  private final ScheduleStudentSelector studentSelector;
  private final ScheduleView scheduleView;

  ScheduleCourseAdder(
      ScheduleService scheduleService,
      ScheduleStudentSelector studentSelector,
      ScheduleView scheduleView) {
    this.scheduleService = Objects.requireNonNull(scheduleService);
    this.studentSelector = Objects.requireNonNull(studentSelector);
    this.scheduleView = Objects.requireNonNull(scheduleView);
  }

  void show() {
    Student student = studentSelector.askForStudent();
    if (student == null) {
      ConsolePause.waitForEnter();
      return;
    }

    try {
      ScheduleService.AvailableCourses courses = scheduleService.getAvailableCoursesForStudent(student.getId());

      scheduleView.printAvailableCourses(
          student,
          courses.getGeneralCourses(),
          courses.getMajorCourses());
      addCoursesUntilBack(student);
    } catch (IllegalArgumentException | IllegalStateException e) {
      ConsoleMessagePrinter.error(e);
      ConsolePause.waitForEnter();
    }
  }

  private void addCoursesUntilBack(Student student) {
    while (true) {
      String courseId = ConsolePrompt.courseIdOrBack("\nEnter Course ID to add (B to back): ");
      if (courseId.equals("B")) {
        return;
      }

      try {
        scheduleService.addCourse(student.getId(), courseId);
        ConsoleMessagePrinter.success("Added successfully");
      } catch (IllegalArgumentException | IllegalStateException e) {
        ConsoleMessagePrinter.error(e);
      }
    }
  }
}
