package vn.edu.studentmanagement.ui.view;

import java.util.List;

import vn.edu.studentmanagement.model.Course;
import vn.edu.studentmanagement.model.CourseDefinition;
import vn.edu.studentmanagement.model.Student;
import vn.edu.studentmanagement.ui.console.ConsoleIO;
import vn.edu.studentmanagement.ui.console.ConsoleMessagePrinter;
import vn.edu.studentmanagement.ui.renderer.CourseTableRenderer;

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
