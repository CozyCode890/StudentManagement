package vn.edu.studentmanagement.model;

public class CourseDefinition {
  private String courseId;
  private String name;
  private CourseType type;
  private Major major; // only for MAJOR courses; null for GENERAL

  public CourseDefinition() {}

  public CourseDefinition(String courseId, String name, CourseType type, Major major) {
    this.courseId = courseId;
    this.name = name;
    this.type = type;
    this.major = major;
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
}
