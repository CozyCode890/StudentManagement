package vn.edu.studentmanagement.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Schedule {
  private static final int MAX_COURSES_PER_SCHEDULE = 3;

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

  public boolean containsCourse(String courseId) {
    return selectedCourses.stream()
        .anyMatch(course -> course.getCourseId().equals(courseId));
  }

  public boolean isFull() {
    return selectedCourses.size() >= MAX_COURSES_PER_SCHEDULE;
  }

  public boolean hasConflictWith(Course course) {
    TimeSlot proposedTime = course.getTimeSlot();

    for (Course selected : selectedCourses) {
      TimeSlot selectedTime = selected.getTimeSlot();
      if (selectedTime != null && selectedTime.overlaps(proposedTime)) {
        return true;
      }
    }

    return false;
  }

  public boolean canAddCourse(Course course) {
    return !containsCourse(course.getCourseId())
        && !isFull()
        && !hasConflictWith(course);
  }

}
