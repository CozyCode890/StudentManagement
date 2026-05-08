package vn.edu.studentmanagement.ui;

import java.util.Objects;

import vn.edu.studentmanagement.model.Student;
import vn.edu.studentmanagement.storage.CourseCatalog;

class ScheduleCourseCatalogView {
  private final CourseCatalog courseCatalog;

  ScheduleCourseCatalogView(CourseCatalog courseCatalog) {
    this.courseCatalog = Objects.requireNonNull(courseCatalog);
  }

  void showAvailableCoursesFor(Student student) {
    System.out.println("\n--- AVAILABLE COURSES FOR " + student.getMajor() + " ---");
    System.out.println("\nGeneral courses:");
    CourseTableRenderer.renderDefinitions(courseCatalog.getGeneralCourses());
    System.out.println("\nMajor courses:");
    CourseTableRenderer.renderDefinitions(courseCatalog.getMajorCoursesByStudentMajor(student.getMajor()));
  }
}
