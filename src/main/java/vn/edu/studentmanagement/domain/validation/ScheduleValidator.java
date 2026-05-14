package vn.edu.studentmanagement.domain.validation;

import java.util.Objects;

import vn.edu.studentmanagement.domain.model.Course;
import vn.edu.studentmanagement.domain.model.Major;
import vn.edu.studentmanagement.domain.model.Schedule;
import vn.edu.studentmanagement.domain.catalog.CourseCatalog;

public class ScheduleValidator {
  private final CourseCatalog courseCatalog;

  public ScheduleValidator(CourseCatalog courseCatalog) {
    this.courseCatalog = Objects.requireNonNull(courseCatalog);
  }

  public void validateCourseId(String courseId) {
    if (courseId == null || courseId.isBlank()) {
      throw new IllegalArgumentException("Course ID cannot be empty.");
    }
  }

  public void validateCourseAllowedForMajor(Course course, Major studentMajor) {
    if (!courseCatalog.isEligibleForMajor(course, studentMajor)) {
      throw new IllegalArgumentException("Course not allowed for student's major");
    }
  }

  public void validateCourseCanBeAdded(Schedule schedule, String courseId, Course selectedCourse) {
    if (schedule.containsCourse(courseId)) {
      throw new IllegalArgumentException("Course already added");
    }

    if (schedule.isFull()) {
      throw new IllegalArgumentException("Max 3 courses");
    }

    if (schedule.hasConflictWith(selectedCourse)) {
      throw new IllegalArgumentException("Conflict time");
    }
  }
}
