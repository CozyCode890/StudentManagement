package vn.edu.studentmanagement.domain.model;

import java.util.Objects;

public class Course {
  private final String courseId;
  private final String name;
  private final CourseType type;
  private final Major major;
  private final TimeSlot timeSlot;

  public Course(String courseId, String name, CourseType type, Major major, TimeSlot timeSlot) {
    this.courseId = courseId;
    this.name = name;
    this.type = type;
    this.major = major;
    this.timeSlot = timeSlot;
  }

  public String getCourseId() {
    return courseId;
  }

  public String getName() {
    return name;
  }

  public CourseType getType() {
    return type;
  }

  public Major getMajor() {
    return major;
  }

  public TimeSlot getTimeSlot() {
    return timeSlot;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof Course other))
      return false;
    return Objects.equals(courseId, other.courseId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(courseId);
  }
}
