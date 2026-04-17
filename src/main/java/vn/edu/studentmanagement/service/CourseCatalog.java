package vn.edu.studentmanagement.service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vn.edu.studentmanagement.model.CourseDefinition;
import vn.edu.studentmanagement.model.CourseType;
import vn.edu.studentmanagement.model.Major;
import vn.edu.studentmanagement.model.TimeSlot;

/**
 * Hardcoded course catalog for now (you can switch to CSV/JSON later).
 */
public class CourseCatalog {
  private final Map<String, CourseDefinition> byCourseId;

  public CourseCatalog() {
    Map<String, CourseDefinition> tmp = new HashMap<>();
    TimeSlot monMorning = new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(9, 30));
    TimeSlot monMid = new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 30));
    TimeSlot monAfternoon = new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(13, 0), LocalTime.of(14, 30));

    TimeSlot tueMorning = new TimeSlot(DayOfWeek.TUESDAY, LocalTime.of(8, 0), LocalTime.of(9, 30));
    TimeSlot tueMid = new TimeSlot(DayOfWeek.TUESDAY, LocalTime.of(10, 0), LocalTime.of(11, 30));
    TimeSlot tueAfternoon = new TimeSlot(DayOfWeek.TUESDAY, LocalTime.of(14, 0), LocalTime.of(15, 30));
    TimeSlot wedMorning = new TimeSlot(DayOfWeek.WEDNESDAY, LocalTime.of(8, 0), LocalTime.of(9, 30));
    TimeSlot wedMid = new TimeSlot(DayOfWeek.WEDNESDAY, LocalTime.of(10, 0), LocalTime.of(11, 30));
    TimeSlot wedAfternoon = new TimeSlot(DayOfWeek.WEDNESDAY, LocalTime.of(13, 0), LocalTime.of(14, 30));

    TimeSlot thurMorning = new TimeSlot(DayOfWeek.THURSDAY, LocalTime.of(8, 0), LocalTime.of(9, 30));
    TimeSlot thurMid = new TimeSlot(DayOfWeek.THURSDAY, LocalTime.of(10, 0), LocalTime.of(11, 30));
    TimeSlot thurAfternoon = new TimeSlot(DayOfWeek.THURSDAY, LocalTime.of(14, 0), LocalTime.of(15, 30));
    TimeSlot friMorning = new TimeSlot(DayOfWeek.FRIDAY, LocalTime.of(8, 0), LocalTime.of(9, 30));
    TimeSlot friMid = new TimeSlot(DayOfWeek.FRIDAY, LocalTime.of(10, 0), LocalTime.of(11, 30));
    TimeSlot friAfternoon = new TimeSlot(DayOfWeek.FRIDAY, LocalTime.of(13, 0), LocalTime.of(14, 30));

    TimeSlot satMorning = new TimeSlot(DayOfWeek.SATURDAY, LocalTime.of(8, 0), LocalTime.of(9, 30));
    TimeSlot satMid = new TimeSlot(DayOfWeek.SATURDAY, LocalTime.of(10, 0), LocalTime.of(11, 30));
    TimeSlot satAfternoon = new TimeSlot(DayOfWeek.SATURDAY, LocalTime.of(14, 0), LocalTime.of(15, 30));
    // General courses (fixed)
    tmp.put("GEN101", new CourseDefinition("GEN101", "Calculus", CourseType.GENERAL, null));
    tmp.put("GEN102", new CourseDefinition("GEN102", "Physics", CourseType.GENERAL, null));
    tmp.put("GEN103", new CourseDefinition("GEN103", "Chemistry", CourseType.GENERAL, null));
    tmp.put("GEN104", new CourseDefinition("GEN104", "HCM Thought", CourseType.GENERAL, null));

    // Major courses (example sets for IT/CS/DS)
    tmp.put("IT201", new CourseDefinition("IT201", "Programming Fundamentals", CourseType.MAJOR, Major.IT));
    tmp.put("IT202", new CourseDefinition("IT202", "Database Systems", CourseType.MAJOR, Major.IT));
    tmp.put("IT203", new CourseDefinition("IT203", "Data Structures", CourseType.MAJOR, Major.IT));

    tmp.put("CS201", new CourseDefinition("CS201", "Algorithms", CourseType.MAJOR, Major.CS));
    tmp.put("CS202", new CourseDefinition("CS202", "Software Engineering", CourseType.MAJOR, Major.CS));
    tmp.put("CS203", new CourseDefinition("CS203", "Computer Networks", CourseType.MAJOR, Major.CS));

    tmp.put("DS201", new CourseDefinition("DS201", "Machine Learning Basics", CourseType.MAJOR, Major.DS));
    tmp.put("DS202", new CourseDefinition("DS202", "Statistics", CourseType.MAJOR, Major.DS));
    tmp.put("DS203", new CourseDefinition("DS203", "Data Mining", CourseType.MAJOR, Major.DS));

    byCourseId = tmp;
  }

  public CourseDefinition findByCourseId(String courseId) {
    if (courseId == null || courseId.isBlank()) return null;
    return byCourseId.get(courseId.trim());
  }

  public List<CourseDefinition> getGeneralCourses() {
    List<CourseDefinition> result = new ArrayList<>();
    for (CourseDefinition def : byCourseId.values()) {
      if (def.getType() == CourseType.GENERAL) result.add(def);
    }
    return result;
  }

  public List<CourseDefinition> getMajorCoursesByStudentMajor(Major major) {
    if (major == null) return Collections.emptyList();
    List<CourseDefinition> result = new ArrayList<>();
    for (CourseDefinition def : byCourseId.values()) {
      if (def.getType() == CourseType.MAJOR && def.getMajor() == major) {
        result.add(def);
      }
    }
    return result;
  }

  public List<CourseDefinition> getAvailableCoursesForStudentMajor(Major major) {
    List<CourseDefinition> all = new ArrayList<>();
    all.addAll(getGeneralCourses());
    all.addAll(getMajorCoursesByStudentMajor(major));
    return all;
  }

  /**
   * Candidate slots for "auto random" mode.
   */
  public List<TimeSlot> getValidTimeSlots() {
    List<TimeSlot> slots = new ArrayList<>();
    // Mon-Fri
    DayOfWeek[] days = new DayOfWeek[] {
        DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY
    };

    // 4 blocks/day
    LocalTime[] starts = new LocalTime[] {
        LocalTime.of(7, 0),
        LocalTime.of(9, 30),
        LocalTime.of(13, 0),
        LocalTime.of(15, 30)
    };
    int durationMinutes = 90;

    for (DayOfWeek day : days) {
      for (LocalTime start : starts) {
        LocalTime end = start.plusMinutes(durationMinutes);
        slots.add(new TimeSlot(day, start, end));
      }
    }
    return slots;
  }
}
