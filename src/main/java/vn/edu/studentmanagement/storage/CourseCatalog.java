package vn.edu.studentmanagement.storage;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import vn.edu.studentmanagement.model.Course;
import vn.edu.studentmanagement.model.CourseDefinition;
import vn.edu.studentmanagement.model.CourseType;
import vn.edu.studentmanagement.model.Major;
import vn.edu.studentmanagement.model.TimeSlot;


public class CourseCatalog {
  private final Map<String, CourseDefinition> byCourseId;
  private final Map<String, TimeSlot> timeSlotsByCourseId;

  public CourseCatalog() {
    Map<String, CourseDefinition> tmp = new LinkedHashMap<>();
    Map<String, TimeSlot> slots = new LinkedHashMap<>();

    TimeSlot monMorning = new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(9, 30));
    TimeSlot monMid = new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 30));
    TimeSlot monAfternoon = new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(13, 0), LocalTime.of(14, 30));

    TimeSlot tueMorning = new TimeSlot(DayOfWeek.TUESDAY, LocalTime.of(8, 0), LocalTime.of(9, 30));
    TimeSlot tueMid = new TimeSlot(DayOfWeek.TUESDAY, LocalTime.of(10, 0), LocalTime.of(11, 30));
    TimeSlot wedMorning = new TimeSlot(DayOfWeek.WEDNESDAY, LocalTime.of(8, 0), LocalTime.of(9, 30));
    TimeSlot wedMid = new TimeSlot(DayOfWeek.WEDNESDAY, LocalTime.of(10, 0), LocalTime.of(11, 30));

    TimeSlot thurMorning = new TimeSlot(DayOfWeek.THURSDAY, LocalTime.of(8, 0), LocalTime.of(9, 30));
    TimeSlot thurMid = new TimeSlot(DayOfWeek.THURSDAY, LocalTime.of(10, 0), LocalTime.of(11, 30));
    TimeSlot friMid = new TimeSlot(DayOfWeek.FRIDAY, LocalTime.of(10, 0), LocalTime.of(11, 30));
    TimeSlot friAfternoon = new TimeSlot(DayOfWeek.FRIDAY, LocalTime.of(13, 0), LocalTime.of(14, 30));

    TimeSlot satMorning = new TimeSlot(DayOfWeek.SATURDAY, LocalTime.of(8, 0), LocalTime.of(9, 30));
    TimeSlot satAfternoon = new TimeSlot(DayOfWeek.SATURDAY, LocalTime.of(14, 0), LocalTime.of(15, 30));

    tmp.put("GEN101", new CourseDefinition("GEN101", "Calculus", CourseType.GENERAL, null));
    slots.put("GEN101", monMorning);
    tmp.put("GEN102", new CourseDefinition("GEN102", "Physics", CourseType.GENERAL, null));
    slots.put("GEN102", tueMorning);
    tmp.put("GEN103", new CourseDefinition("GEN103", "Chemistry", CourseType.GENERAL, null));
    slots.put("GEN103", wedMorning);
    tmp.put("GEN104", new CourseDefinition("GEN104", "HCM Thought", CourseType.GENERAL, null));
    slots.put("GEN104", thurMorning);

    tmp.put("IT201", new CourseDefinition("IT201", "Programming Fundamentals", CourseType.MAJOR, Major.IT));
    slots.put("IT201", monMid);
    tmp.put("IT202", new CourseDefinition("IT202", "Database Systems", CourseType.MAJOR, Major.IT));
    slots.put("IT202", wedMid);
    tmp.put("IT203", new CourseDefinition("IT203", "Data Structures", CourseType.MAJOR, Major.IT));
    slots.put("IT203", friMid);

    tmp.put("CS201", new CourseDefinition("CS201", "Algorithms", CourseType.MAJOR, Major.CS));
    slots.put("CS201", monAfternoon);
    tmp.put("CS202", new CourseDefinition("CS202", "Software Engineering", CourseType.MAJOR, Major.CS));
    slots.put("CS202", thurMid);
    tmp.put("CS203", new CourseDefinition("CS203", "Computer Networks", CourseType.MAJOR, Major.CS));
    slots.put("CS203", satMorning);

    tmp.put("DS201", new CourseDefinition("DS201", "Machine Learning Basics", CourseType.MAJOR, Major.DS));
    slots.put("DS201", tueMid);
    tmp.put("DS202", new CourseDefinition("DS202", "Statistics", CourseType.MAJOR, Major.DS));
    slots.put("DS202", friAfternoon);
    tmp.put("DS203", new CourseDefinition("DS203", "Data Mining", CourseType.MAJOR, Major.DS));
    slots.put("DS203", satAfternoon);

    byCourseId = tmp;
    timeSlotsByCourseId = slots;
  }

  public CourseDefinition findByCourseId(String courseId) {
    if (courseId == null || courseId.isBlank()) return null;
    return byCourseId.get(normalizeCourseId(courseId));
  }

  public Course createScheduledCourse(String courseId) {
    CourseDefinition def = findByCourseId(courseId);
    if (def == null) return null;

    TimeSlot timeSlot = timeSlotsByCourseId.get(def.getCourseId());
    if (timeSlot == null) {
      throw new IllegalStateException("Course has no scheduled time slot: " + def.getCourseId());
    }

    return new Course(
        def.getCourseId(),
        def.getName(),
        def.getType(),
        def.getMajor(),
        timeSlot);
  }

  public boolean isEligibleForMajor(CourseDefinition def, Major studentMajor) {
    if (def == null) return false;
    if (def.getType() == CourseType.GENERAL) return true;
    return studentMajor != null && def.getType() == CourseType.MAJOR && def.getMajor() == studentMajor;
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
    for (CourseDefinition def : byCourseId.values()) {
      if (isEligibleForMajor(def, major)) {
        all.add(def);
      }
    }
    return all;
  }


  public List<TimeSlot> getValidTimeSlots() {
    return new ArrayList<>(timeSlotsByCourseId.values());
  }

  private String normalizeCourseId(String courseId) {
    return courseId.trim().toUpperCase(Locale.ROOT);
  }
}
