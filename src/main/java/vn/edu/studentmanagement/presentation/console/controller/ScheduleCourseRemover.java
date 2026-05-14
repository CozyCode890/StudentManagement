package vn.edu.studentmanagement.presentation.console.controller;

import java.util.List;
import java.util.Objects;

import vn.edu.studentmanagement.domain.model.Course;
import vn.edu.studentmanagement.domain.model.Student;
import vn.edu.studentmanagement.application.schedule.ScheduleService;
import vn.edu.studentmanagement.presentation.console.io.ConsoleMessagePrinter;
import vn.edu.studentmanagement.presentation.console.io.ConsolePause;
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

    removeCoursesUntilBack(student);
  }

  private void removeCoursesUntilBack(Student student) {
    while (true) {
      List<Course> currentCourses = loadCurrentCourses(student);
      if (currentCourses == null) {
        return;
      }

      if (currentCourses.isEmpty()) {
        ConsoleMessagePrinter.warning("This student has no courses to remove.");
        ConsolePause.waitForEnter();
        return;
      }

      CourseTableRenderer.renderNumberedCourses(currentCourses, 1);
      String courseId = ConsolePrompt.courseIdOrBack("\nEnter Course ID to remove (B or 0 to back): ");
      if (courseId.equals("B") || courseId.equals("0")) {
        return;
      }

      Course selectedCourse = findCourseById(currentCourses, courseId);
      if (selectedCourse == null) {
        ConsoleMessagePrinter.error("Course ID not found in student's schedule.");
        continue;
      }

      if (confirmRemoval(selectedCourse)) {
        removeCourse(student, selectedCourse.getCourseId());
      } else {
        ConsoleMessagePrinter.warning("Removal canceled.");
      }
    }
  }

  private List<Course> loadCurrentCourses(Student student) {
    try {
      return scheduleService.findCoursesByStudentId(student.getId());
    } catch (IllegalArgumentException | IllegalStateException e) {
      ConsoleMessagePrinter.error(e);
      ConsolePause.waitForEnter();
      return null;
    }
  }

  private void removeCourse(Student student, String courseId) {
    try {
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

  private Course findCourseById(List<Course> courses, String courseId) {
    return courses.stream()
        .filter(course -> course.getCourseId().equals(courseId))
        .findFirst()
        .orElse(null);
  }

  private boolean confirmRemoval(Course course) {
    System.out.println();
    CourseTableRenderer.renderNumberedCourses(List.of(course), 1);
    String confirm = ConsolePrompt.upperTrimmed("Confirm remove this course? (Y/N): ");
    return confirm.equals("Y");
  }
}
