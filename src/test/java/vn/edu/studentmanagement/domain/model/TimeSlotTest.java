package vn.edu.studentmanagement.domain.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

class TimeSlotTest {
  @Test
  void overlapsReturnsTrueWhenTimesIntersectOnSameDay() {
    TimeSlot morning = slot(DayOfWeek.MONDAY, 8, 10);
    TimeSlot overlapping = slot(DayOfWeek.MONDAY, 9, 11);

    assertTrue(morning.overlaps(overlapping));
  }

  @Test
  void overlapsReturnsFalseWhenTimesTouchButDoNotIntersect() {
    TimeSlot first = slot(DayOfWeek.MONDAY, 8, 10);
    TimeSlot second = slot(DayOfWeek.MONDAY, 10, 12);

    assertFalse(first.overlaps(second));
  }

  @Test
  void overlapsReturnsFalseForDifferentDaysOrNull() {
    TimeSlot monday = slot(DayOfWeek.MONDAY, 8, 10);
    TimeSlot tuesday = slot(DayOfWeek.TUESDAY, 9, 11);

    assertFalse(monday.overlaps(tuesday));
    assertFalse(monday.overlaps(null));
  }

  private TimeSlot slot(DayOfWeek day, int startHour, int endHour) {
    return new TimeSlot(day, LocalTime.of(startHour, 0), LocalTime.of(endHour, 0));
  }
}
