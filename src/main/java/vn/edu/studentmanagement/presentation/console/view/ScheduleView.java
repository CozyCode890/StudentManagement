package vn.edu.studentmanagement.presentation.console.view;

import java.util.List;

import vn.edu.studentmanagement.domain.model.Course;
import vn.edu.studentmanagement.domain.model.CourseDefinition;
import vn.edu.studentmanagement.domain.model.Student;
import vn.edu.studentmanagement.presentation.console.io.ConsoleIO;
import vn.edu.studentmanagement.presentation.console.io.ConsoleMessagePrinter;
import vn.edu.studentmanagement.presentation.console.renderer.CourseTableRenderer;

public class ScheduleView {
  public void printSchedule(Student student, List<Course> courses) {
    ConsoleIO.println("\n>>> SCHEDULE FOR: " + student.getFullName().toUpperCase() + " (ID: " + student.getId() + ")");
    if (courses.isEmpty()) {
      ConsoleMessagePrinter.warning("No courses registered yet");
      return;
    }

    CourseTableRenderer.renderCourses(courses);
  }

  public void printAvailableCourses(
      Student student,
      List<CourseDefinition> generalCourses,
      List<CourseDefinition> majorCourses) {
    ConsoleIO.println("\n--- AVAILABLE COURSES FOR " + student.getMajor() + " ---");
    ConsoleIO.println("\nGeneral courses:");
    CourseTableRenderer.renderDefinitions(generalCourses);
    ConsoleIO.println("\nMajor courses:");
    CourseTableRenderer.renderDefinitions(majorCourses);
  }
}
