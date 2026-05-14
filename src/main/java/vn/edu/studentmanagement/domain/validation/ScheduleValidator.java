package vn.edu.studentmanagement.domain.validation;

import java.util.Objects;

import vn.edu.studentmanagement.domain.model.Course;
import vn.edu.studentmanagement.domain.model.CourseType;
import vn.edu.studentmanagement.domain.model.Major;
import vn.edu.studentmanagement.domain.model.Schedule;
import vn.edu.studentmanagement.domain.model.TimeSlot;
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
    if (!isEligibleForMajor(course, studentMajor)) {
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

    if (!isValidTimeSlot(selectedCourse.getTimeSlot())) {
      throw new IllegalArgumentException("Course scheduled outside valid time slots");
    }
  }

  private boolean isEligibleForMajor(Course course, Major studentMajor) {
    if (course == null) {
      return false;
    }
    if (course.getType() == CourseType.GENERAL) {
      return true;
    }
    return studentMajor != null && course.getType() == CourseType.MAJOR && course.getMajor() == studentMajor;
  }

  private boolean isValidTimeSlot(TimeSlot timeSlot) {
    return timeSlot != null && courseCatalog.getValidTimeSlots().contains(timeSlot);
  }
}
