package vn.edu.studentmanagement.presentation.console.controller;

import java.util.Objects;

import vn.edu.studentmanagement.domain.model.Student;
import vn.edu.studentmanagement.application.schedule.ScheduleService;
import vn.edu.studentmanagement.domain.catalog.CourseCatalog;
import vn.edu.studentmanagement.presentation.console.io.ConsoleMessagePrinter;
import vn.edu.studentmanagement.presentation.console.io.ConsolePause;
import vn.edu.studentmanagement.presentation.console.io.ConsolePrompt;
import vn.edu.studentmanagement.presentation.console.view.ScheduleView;

class ScheduleCourseAdder {
  private final ScheduleService scheduleService;
  private final CourseCatalog courseCatalog;
  private final ScheduleStudentSelector studentSelector;
  private final ScheduleView scheduleView;

  ScheduleCourseAdder(
      ScheduleService scheduleService,
      CourseCatalog courseCatalog,
      ScheduleStudentSelector studentSelector,
      ScheduleView scheduleView) {
    this.scheduleService = Objects.requireNonNull(scheduleService);
    this.courseCatalog = Objects.requireNonNull(courseCatalog);
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
      scheduleView.printAvailableCourses(
          student,
          courseCatalog.getGeneralCourses(),
          courseCatalog.getMajorCoursesByStudentMajor(student.getMajor()));
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

      ScheduleService.AddCourseResult result = scheduleService.addCourse(student.getId(), courseId);
      if (result.isSuccess()) {
        ConsoleMessagePrinter.success(result.getMessage());
      } else {
        ConsoleMessagePrinter.error(result.getMessage());
      }
    }
  }
}
