package vn.edu.studentmanagement.domain.catalog;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import vn.edu.studentmanagement.domain.model.Course;
import vn.edu.studentmanagement.domain.model.CourseType;
import vn.edu.studentmanagement.domain.model.Major;
import vn.edu.studentmanagement.domain.model.TimeSlot;
import vn.edu.studentmanagement.domain.normalization.CourseNormalizer;

public class CourseCatalog {
  private final Map<String, Course> byCourseId;
  private final CourseNormalizer courseNormalizer = new CourseNormalizer();

  public CourseCatalog() {
    Map<String, Course> tmp = new LinkedHashMap<>();

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

    tmp.put("GEN101", new Course("GEN101", "Calculus", CourseType.GENERAL, null, monMorning));
    tmp.put("GEN102", new Course("GEN102", "Physics", CourseType.GENERAL, null, tueMorning));
    tmp.put("GEN103", new Course("GEN103", "Chemistry", CourseType.GENERAL, null, wedMorning));
    tmp.put("GEN104", new Course("GEN104", "HCM Thought", CourseType.GENERAL, null, thurMorning));

    tmp.put("IT201", new Course("IT201", "Programming Fundamentals", CourseType.MAJOR, Major.IT, monMid));
    tmp.put("IT202", new Course("IT202", "Database Systems", CourseType.MAJOR, Major.IT, wedMid));
    tmp.put("IT203", new Course("IT203", "Data Structures", CourseType.MAJOR, Major.IT, friMid));

    tmp.put("CS201", new Course("CS201", "Algorithms", CourseType.MAJOR, Major.CS, monAfternoon));
    tmp.put("CS202", new Course("CS202", "Software Engineering", CourseType.MAJOR, Major.CS, thurMid));
    tmp.put("CS203", new Course("CS203", "Computer Networks", CourseType.MAJOR, Major.CS, satMorning));

    tmp.put("DS201", new Course("DS201", "Machine Learning Basics", CourseType.MAJOR, Major.DS, tueMid));
    tmp.put("DS202", new Course("DS202", "Statistics", CourseType.MAJOR, Major.DS, friAfternoon));
    tmp.put("DS203", new Course("DS203", "Data Mining", CourseType.MAJOR, Major.DS, satAfternoon));

    byCourseId = Collections.unmodifiableMap(new LinkedHashMap<>(tmp));
  }

  public Course findByCourseId(String courseId) {
    if (courseId == null || courseId.isBlank())
      return null;
    return byCourseId.get(courseNormalizer.normalizeCourseId(courseId));
  }

  public boolean isEligibleForMajor(Course course, Major studentMajor) {
    if (course == null)
      return false;
    if (course.getType() == CourseType.GENERAL)
      return true;
    return studentMajor != null && course.getType() == CourseType.MAJOR && course.getMajor() == studentMajor;
  }

  public List<Course> getGeneralCourses() {
    List<Course> result = new ArrayList<>();
    for (Course course : byCourseId.values()) {
      if (course.getType() == CourseType.GENERAL)
        result.add(course);
    }
    return result;
  }

  public List<Course> getMajorCoursesByStudentMajor(Major major) {
    if (major == null)
      return Collections.emptyList();
    List<Course> result = new ArrayList<>();
    for (Course course : byCourseId.values()) {
      if (course.getType() == CourseType.MAJOR && course.getMajor() == major) {
        result.add(course);
      }
    }
    return result;
  }

  public List<TimeSlot> getValidTimeSlots() {
    List<TimeSlot> result = new ArrayList<>();
    for (Course course : byCourseId.values()) {
      result.add(course.getTimeSlot());
    }
    return result;
  }

}
