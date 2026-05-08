package vn.edu.studentmanagement.app;

import java.util.List;
import java.util.Objects;

import vn.edu.studentmanagement.model.Course;
import vn.edu.studentmanagement.model.Student;
import vn.edu.studentmanagement.service.ScheduleService;
import vn.edu.studentmanagement.ui.console.ConsoleMessagePrinter;
import vn.edu.studentmanagement.ui.view.ScheduleView;

class ScheduleViewer {
  private final ScheduleService scheduleService;
  private final ScheduleStudentSelector studentSelector;
  private final ScheduleView scheduleView;

  ScheduleViewer(ScheduleService scheduleService, ScheduleStudentSelector studentSelector, ScheduleView scheduleView) {
    this.scheduleService = Objects.requireNonNull(scheduleService);
    this.studentSelector = Objects.requireNonNull(studentSelector);
    this.scheduleView = Objects.requireNonNull(scheduleView);
  }

  void show() {
    Student student = studentSelector.askForStudent();
    if (student == null) {
      return;
    }

    try {
      List<Course> courses = scheduleService.getScheduleSortedByDayThenStart(student.getId());
      scheduleView.printSchedule(student, courses);
    } catch (IllegalArgumentException | IllegalStateException e) {
      ConsoleMessagePrinter.error(e);
    }
  }
}
