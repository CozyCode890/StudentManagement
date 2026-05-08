package vn.edu.studentmanagement.ui;

import java.util.List;
import java.util.Objects;

import vn.edu.studentmanagement.model.Course;
import vn.edu.studentmanagement.model.Student;
import vn.edu.studentmanagement.service.ScheduleService;

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
      List<Course> currentCourses = scheduleService.getSchedule(student.getId()).getSelectedCourses();
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
