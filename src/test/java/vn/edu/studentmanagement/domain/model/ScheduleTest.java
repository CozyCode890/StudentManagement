package vn.edu.studentmanagement.domain.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

class ScheduleTest {
  @Test
  void containsCourseReturnsTrueWhenCourseAlreadySelected() {
    Schedule schedule = new Schedule("ITITIU21001");
    schedule.addCourse(course("IT101", DayOfWeek.MONDAY, 8, 10));

    assertTrue(schedule.containsCourse("IT101"));
    assertFalse(schedule.containsCourse("IT102"));
  }

  @Test
  void isFullReturnsTrueAfterThreeCourses() {
    Schedule schedule = new Schedule("ITITIU21001");
    schedule.addCourse(course("IT101", DayOfWeek.MONDAY, 8, 10));
    schedule.addCourse(course("IT102", DayOfWeek.TUESDAY, 8, 10));
    schedule.addCourse(course("IT103", DayOfWeek.WEDNESDAY, 8, 10));

    assertTrue(schedule.isFull());
  }

  @Test
  void hasConflictWithReturnsTrueWhenNewCourseOverlapsSelectedCourse() {
    Schedule schedule = new Schedule("ITITIU21001");
    schedule.addCourse(course("IT101", DayOfWeek.MONDAY, 8, 10));

    assertTrue(schedule.hasConflictWith(course("IT102", DayOfWeek.MONDAY, 9, 11)));
    assertFalse(schedule.hasConflictWith(course("IT103", DayOfWeek.MONDAY, 10, 12)));
  }

  @Test
  void canAddCourseReturnsFalseForDuplicateFullOrConflictingCourse() {
    Schedule duplicateSchedule = new Schedule("ITITIU21001");
    duplicateSchedule.addCourse(course("IT101", DayOfWeek.MONDAY, 8, 10));
    assertFalse(duplicateSchedule.canAddCourse(course("IT101", DayOfWeek.TUESDAY, 8, 10)));

    Schedule fullSchedule = new Schedule("ITITIU21001");
    fullSchedule.addCourse(course("IT101", DayOfWeek.MONDAY, 8, 10));
    fullSchedule.addCourse(course("IT102", DayOfWeek.TUESDAY, 8, 10));
    fullSchedule.addCourse(course("IT103", DayOfWeek.WEDNESDAY, 8, 10));
    assertFalse(fullSchedule.canAddCourse(course("IT104", DayOfWeek.THURSDAY, 8, 10)));

    Schedule conflictSchedule = new Schedule("ITITIU21001");
    conflictSchedule.addCourse(course("IT101", DayOfWeek.MONDAY, 8, 10));
    assertFalse(conflictSchedule.canAddCourse(course("IT102", DayOfWeek.MONDAY, 9, 11)));
  }

  @Test
  void canAddCourseReturnsTrueForNewAvailableNonConflictingCourse() {
    Schedule schedule = new Schedule("ITITIU21001");
    schedule.addCourse(course("IT101", DayOfWeek.MONDAY, 8, 10));

    assertTrue(schedule.canAddCourse(course("IT102", DayOfWeek.TUESDAY, 8, 10)));
  }

  private Course course(String courseId, DayOfWeek day, int startHour, int endHour) {
    return new Course(
        courseId,
        "Course " + courseId,
        CourseType.GENERAL,
        null,
        new TimeSlot(day, LocalTime.of(startHour, 0), LocalTime.of(endHour, 0)));
  }
}
