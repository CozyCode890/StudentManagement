package vn.edu.studentmanagement.model;

import java.util.Objects;


public class Course {
  private String courseId;
  private String name;
  private CourseType type;
  private Major major;
  private TimeSlot timeSlot;

  public Course() {}

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

  public void setCourseId(String courseId) {
    this.courseId = courseId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public CourseType getType() {
    return type;
  }

  public void setType(CourseType type) {
    this.type = type;
  }

  public Major getMajor() {
    return major;
  }

  public void setMajor(Major major) {
    this.major = major;
  }

  public TimeSlot getTimeSlot() {
    return timeSlot;
  }

  public void setTimeSlot(TimeSlot timeSlot) {
    this.timeSlot = timeSlot;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Course other)) return false;
    return Objects.equals(courseId, other.courseId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(courseId);
  }
}
