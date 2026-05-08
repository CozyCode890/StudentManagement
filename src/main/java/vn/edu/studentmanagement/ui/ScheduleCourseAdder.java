package vn.edu.studentmanagement.ui;

import java.util.Objects;

import vn.edu.studentmanagement.model.Student;
import vn.edu.studentmanagement.service.ScheduleService;

class ScheduleCourseAdder {
  private final ScheduleService scheduleService;
  private final ScheduleStudentSelector studentSelector;
  private final ScheduleCourseCatalogView catalogView;

  ScheduleCourseAdder(
      ScheduleService scheduleService,
      ScheduleStudentSelector studentSelector,
      ScheduleCourseCatalogView catalogView) {
    this.scheduleService = Objects.requireNonNull(scheduleService);
    this.studentSelector = Objects.requireNonNull(studentSelector);
    this.catalogView = Objects.requireNonNull(catalogView);
  }

  void show() {
    Student student = studentSelector.askForStudent();
    if (student == null) {
      ConsolePause.waitForEnter();
      return;
    }

    try {
      catalogView.showAvailableCoursesFor(student);
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
