package vn.edu.studentmanagement.domain.model;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Objects;

public class TimeSlot {
  private final DayOfWeek day;
  private final LocalTime start;
  private final LocalTime end;

  public TimeSlot(DayOfWeek day, LocalTime start, LocalTime end) {
    this.day = day;
    this.start = start;
    this.end = end;
  }

  public DayOfWeek getDay() {
    return day;
  }

  public LocalTime getStart() {
    return start;
  }

  public LocalTime getEnd() {
    return end;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof TimeSlot other))
      return false;
    return day == other.day
        && Objects.equals(start, other.start)
        && Objects.equals(end, other.end);
  }

  @Override
  public int hashCode() {
    return Objects.hash(day, start, end);
  }
}
