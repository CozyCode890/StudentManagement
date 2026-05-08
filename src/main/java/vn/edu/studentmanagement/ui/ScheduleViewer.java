package vn.edu.studentmanagement.ui;

import java.util.List;
import java.util.Objects;

import vn.edu.studentmanagement.model.Course;
import vn.edu.studentmanagement.model.Student;
import vn.edu.studentmanagement.service.ScheduleService;

class ScheduleViewer {
  private final ScheduleService scheduleService;
  private final ScheduleStudentSelector studentSelector;

  ScheduleViewer(ScheduleService scheduleService, ScheduleStudentSelector studentSelector) {
    this.scheduleService = Objects.requireNonNull(scheduleService);
    this.studentSelector = Objects.requireNonNull(studentSelector);
  }

  void show() {
    Student student = studentSelector.askForStudent();
    if (student == null) {
      return;
    }

    try {
      List<Course> courses = scheduleService.getScheduleSortedByDayThenStart(student.getId());

      System.out.println("\n>>> SCHEDULE FOR: " + student.getFullName().toUpperCase() + " (ID: " + student.getId() + ")");
      if (courses.isEmpty()) {
        ConsoleMessagePrinter.warning("No courses registered yet");
      } else {
        CourseTableRenderer.renderCourses(courses);
      }
    } catch (IllegalArgumentException | IllegalStateException e) {
      ConsoleMessagePrinter.error(e);
    }
  }
}
