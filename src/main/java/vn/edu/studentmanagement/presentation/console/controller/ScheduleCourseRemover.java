package vn.edu.studentmanagement.presentation.console.controller;

import java.util.List;
import java.util.Objects;

import vn.edu.studentmanagement.domain.model.Course;
import vn.edu.studentmanagement.domain.model.Student;
import vn.edu.studentmanagement.application.schedule.ScheduleService;
import vn.edu.studentmanagement.presentation.console.io.ConsoleMessagePrinter;
import vn.edu.studentmanagement.presentation.console.io.ConsolePrompt;
import vn.edu.studentmanagement.presentation.console.renderer.CourseTableRenderer;

class ScheduleCourseRemover {
  private final ScheduleService scheduleService;
  private final ScheduleStudentSelector studentSelector;

  ScheduleCourseRemover(ScheduleService scheduleService, ScheduleStudentSelector studentSelector) {
    this.scheduleService = Objects.requireNonNull(scheduleService);
    this.studentSelector = Objects.requireNonNull(studentSelector);
  }

  void show() {
    Student student = studentSelector.askForStudent();
    if (student == null) {
      return;
    }

    try {
      List<Course> currentCourses = scheduleService.findCoursesByStudentId(student.getId());
      if (currentCourses.isEmpty()) {
        ConsoleMessagePrinter.warning("This student has no courses to remove.");
        return;
      }

      CourseTableRenderer.renderCourses(currentCourses);
      String courseId = ConsolePrompt.upperTrimmed("\nEnter Course ID to remove: ");

      boolean removed = scheduleService.removeCourse(student.getId(), courseId);
      if (removed) {
        ConsoleMessagePrinter.success("Course removed successfully.");
      } else {
        ConsoleMessagePrinter.error("Course ID not found in student's schedule.");
      }
    } catch (IllegalArgumentException | IllegalStateException e) {
      ConsoleMessagePrinter.error(e);
    }
  }
}
