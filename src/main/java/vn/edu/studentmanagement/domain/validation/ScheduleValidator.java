package vn.edu.studentmanagement.domain.validation;

import java.util.Objects;

import vn.edu.studentmanagement.domain.model.Course;
import vn.edu.studentmanagement.domain.model.CourseDefinition;
import vn.edu.studentmanagement.domain.model.CourseType;
import vn.edu.studentmanagement.domain.model.Major;
import vn.edu.studentmanagement.domain.model.Schedule;
import vn.edu.studentmanagement.domain.model.TimeSlot;
import vn.edu.studentmanagement.domain.catalog.CourseCatalog;

public class ScheduleValidator {
  private static final int MAX_COURSES_PER_SCHEDULE = 3;

  private final CourseCatalog courseCatalog;

  public ScheduleValidator(CourseCatalog courseCatalog) {
    this.courseCatalog = Objects.requireNonNull(courseCatalog);
  }

  public void validateCourseId(String courseId) {
    if (courseId == null || courseId.isBlank()) {
      throw new IllegalArgumentException("Course ID cannot be empty.");
    }
  }

  public void validateCourseAllowedForMajor(CourseDefinition def, Major studentMajor) {
    if (!isEligibleForMajor(def, studentMajor)) {
      throw new IllegalArgumentException("Course not allowed for student's major");
    }
  }

  public void validateCourseCanBeAdded(Schedule schedule, String courseId, Course selectedCourse) {
    for (Course selected : schedule.getSelectedCourses()) {
      if (selected.getCourseId().equals(courseId)) {
        throw new IllegalArgumentException("Course already added");
      }
    }

    if (schedule.selectedCoursesCount() >= MAX_COURSES_PER_SCHEDULE) {
      throw new IllegalArgumentException("Max 3 courses");
    }

    TimeSlot proposedTime = selectedCourse.getTimeSlot();
    for (Course selected : schedule.getSelectedCourses()) {
      if (overlap(selected.getTimeSlot(), proposedTime)) {
        throw new IllegalArgumentException("Conflict time");
      }
    }

    if (!isValidTimeSlot(proposedTime)) {
      throw new IllegalArgumentException("Course scheduled outside valid time slots");
    }
  }

  public boolean overlap(TimeSlot a, TimeSlot b) {
    if (a == null || b == null) {
      return false;
    }
    if (a.getDay() != b.getDay()) {
      return false;
    }
    return a.getStart().compareTo(b.getEnd()) < 0 && b.getStart().compareTo(a.getEnd()) < 0;
  }

  private boolean isEligibleForMajor(CourseDefinition def, Major studentMajor) {
    if (def == null) {
      return false;
    }
    if (def.getType() == CourseType.GENERAL) {
      return true;
    }
    return studentMajor != null && def.getType() == CourseType.MAJOR && def.getMajor() == studentMajor;
  }

  private boolean isValidTimeSlot(TimeSlot timeSlot) {
    return timeSlot != null && courseCatalog.getValidTimeSlots().contains(timeSlot);
  }
}
