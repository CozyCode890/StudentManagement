package vn.edu.studentmanagement.model;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Objects;

public class TimeSlot {
  private DayOfWeek day;
  private LocalTime start;
  private LocalTime end;

  public TimeSlot() {}

  public TimeSlot(DayOfWeek day, LocalTime start, LocalTime end) {
    this.day = day;
    this.start = start;
    this.end = end;
  }

  public DayOfWeek getDay() {
    return day;
  }

  public void setDay(DayOfWeek day) {
    this.day = day;
  }

  public LocalTime getStart() {
    return start;
  }

  public void setStart(LocalTime start) {
    this.start = start;
  }

  public LocalTime getEnd() {
    return end;
  }

  public void setEnd(LocalTime end) {
    this.end = end;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof TimeSlot other)) return false;
    return day == other.day
        && Objects.equals(start, other.start)
        && Objects.equals(end, other.end);
  }

  @Override
  public int hashCode() {
    return Objects.hash(day, start, end);
  }
}
