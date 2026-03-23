package vn.edu.studentmanagement.model;

import java.util.ArrayList;
import java.util.List;

public class Schedule {
  private String studentId;
  private List<Course> selectedCourses = new ArrayList<>();

  public Schedule() {}

  public Schedule(String studentId) {
    this.studentId = studentId;
  }

  public String getStudentId() {
    return studentId;
  }

  public void setStudentId(String studentId) {
    this.studentId = studentId;
  }

  public List<Course> getSelectedCourses() {
    return selectedCourses;
  }

  public void setSelectedCourses(List<Course> selectedCourses) {
    this.selectedCourses = selectedCourses;
  }

  public int selectedCoursesCount() {
    return selectedCourses == null ? 0 : selectedCourses.size();
  }
}
