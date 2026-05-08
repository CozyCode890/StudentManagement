package vn.edu.studentmanagement.presentation.console.renderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import vn.edu.studentmanagement.domain.model.Course;
import vn.edu.studentmanagement.domain.model.CourseDefinition;

public class CourseTableRenderer {
  private CourseTableRenderer() {
  }

  public static void renderCourses(List<Course> courses) {
    List<List<String>> rows = new ArrayList<>();
    for (Course course : courses) {
      String time = course.getTimeSlot().getStart() + "-" + course.getTimeSlot().getEnd();
      rows.add(Arrays.asList(
          course.getCourseId(),
          course.getName(),
          String.valueOf(course.getTimeSlot().getDay()),
          time));
    }

    ConsoleTable.print(List.of("ID", "Course Name", "Day", "Time"), rows);
  }

  public static void renderDefinitions(List<CourseDefinition> definitions) {
    List<List<String>> rows = new ArrayList<>();
    for (CourseDefinition definition : definitions) {
      rows.add(Arrays.asList(
          definition.getCourseId(),
          definition.getName(),
          String.valueOf(definition.getType())));
    }

    ConsoleTable.print(List.of("ID", "Course Name", "Type"), rows);
  }
}
