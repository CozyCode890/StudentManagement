package vn.edu.studentmanagement.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Schedule {
  private final String studentId;
  private final List<Course> selectedCourses = new ArrayList<>();

  public Schedule(String studentId) {
    this.studentId = studentId;
  }

  public String getStudentId() {
    return studentId;
  }

  public List<Course> getSelectedCourses() {
    return Collections.unmodifiableList(selectedCourses);
  }

  public void addCourse(Course course) {
    selectedCourses.add(course);
  }

  public boolean removeCourseById(String courseId) {
    return selectedCourses.removeIf(course -> course.getCourseId().equals(courseId));
  }

  public boolean hasSelectedCourses() {
    return !selectedCourses.isEmpty();
  }

  public int selectedCoursesCount() {
    return selectedCourses.size();
  }
}
