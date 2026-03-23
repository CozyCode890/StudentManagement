package vn.edu.studentmanagement.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import vn.edu.studentmanagement.model.Course;
import vn.edu.studentmanagement.model.CourseDefinition;
import vn.edu.studentmanagement.model.Major;
import vn.edu.studentmanagement.model.Schedule;
import vn.edu.studentmanagement.model.Student;
import vn.edu.studentmanagement.model.TimeSlot;
import vn.edu.studentmanagement.model.CourseType;

public class ScheduleService {
  private final StudentService studentService;
  private final CourseCatalog courseCatalog;

  // In-memory schedule store (can be persisted later).
  private final Map<String, Schedule> schedulesByStudentId = new HashMap<>();

  public static class AddCourseResult {
    private final boolean success;
    private final String message;

    public AddCourseResult(boolean success, String message) {
      this.success = success;
      this.message = message;
    }

    public boolean isSuccess() {
      return success;
    }

    public String getMessage() {
      return message;
    }
  }

  public ScheduleService(StudentService studentService, CourseCatalog courseCatalog) {
    this.studentService = Objects.requireNonNull(studentService);
    this.courseCatalog = Objects.requireNonNull(courseCatalog);
  }

  /**
   * overlap if (same day) && startA < endB && startB < endA
   */
  public boolean overlap(TimeSlot a, TimeSlot b) {
    if (a == null || b == null) return false;
    if (a.getDay() != b.getDay()) return false;
    return a.getStart().compareTo(b.getEnd()) < 0 && b.getStart().compareTo(a.getEnd()) < 0;
  }

  public AddCourseResult addCourse(String studentId, Course course) {
    try {
      if (studentId == null || studentId.isBlank()) {
        throw new IllegalArgumentException("ID cannot be empty.");
      }
      if (course == null) {
        throw new IllegalArgumentException("Course cannot be null.");
      }
      if (course.getTimeSlot() == null) {
        throw new IllegalArgumentException("Time slot is required.");
      }
      String sid = studentId.trim();

      Student student = studentService.findById(sid);
      if (student == null) {
        throw new IllegalArgumentException("ID not found");
      }

      CourseDefinition def = courseCatalog.findByCourseId(course.getCourseId());
      if (def == null) {
        throw new IllegalArgumentException("Course not found");
      }

      Major studentMajor = student.getMajor();

      // Filter major courses by student major
      if (def.getType() == CourseType.MAJOR && def.getMajor() != studentMajor) {
        throw new IllegalArgumentException("Course not allowed for student's major");
      }

      Schedule schedule = schedulesByStudentId.computeIfAbsent(sid, Schedule::new);
      if (schedule.selectedCoursesCount() >= 3) {
        throw new IllegalArgumentException("Max 3 courses");
      }

      // Conflict checking against all selected courses
      for (Course selected : schedule.getSelectedCourses()) {
        if (overlap(selected.getTimeSlot(), course.getTimeSlot())) {
          throw new IllegalArgumentException("Conflict time");
        }
      }

      // Build the final selected course using catalog definition + chosen timeslot.
      Course selectedCourse = new Course(
          def.getCourseId(),
          def.getName(),
          def.getType(),
          def.getMajor(),
          course.getTimeSlot());

      // Prevent duplicate courseId within one schedule.
      for (Course selected : schedule.getSelectedCourses()) {
        if (selected.getCourseId().equals(selectedCourse.getCourseId())) {
          throw new IllegalArgumentException("Course already added");
        }
      }

      schedule.getSelectedCourses().add(selectedCourse);
      return new AddCourseResult(true, "Added successfully");
    } catch (IllegalArgumentException e) {
      return new AddCourseResult(false, e.getMessage());
    }
  }

  public boolean removeCourse(String studentId, String courseId) {
    if (studentId == null || studentId.isBlank()) {
      throw new IllegalArgumentException("ID cannot be empty.");
    }
    if (courseId == null || courseId.isBlank()) {
      throw new IllegalArgumentException("Course id cannot be empty.");
    }
    String sid = studentId.trim();
    String cid = courseId.trim();

    Schedule schedule = schedulesByStudentId.get(sid);
    if (schedule == null) return false;

    boolean removed = schedule.getSelectedCourses().removeIf(c -> c.getCourseId().equals(cid));
    if (schedule.getSelectedCourses().isEmpty()) {
      schedulesByStudentId.remove(sid);
    }
    return removed;
  }

  public Schedule getSchedule(String studentId) {
    if (studentId == null || studentId.isBlank()) {
      throw new IllegalArgumentException("ID cannot be empty.");
    }
    String sid = studentId.trim();
    Schedule schedule = schedulesByStudentId.get(sid);
    if (schedule == null) {
      // Always return a schedule object for consistent UI.
      schedule = new Schedule(sid);
    }
    return schedule;
  }

  public List<Course> getScheduleSortedByDayThenStart(String studentId) {
    List<Course> courses = new ArrayList<>(getSchedule(studentId).getSelectedCourses());
    courses.sort(
        Comparator.comparing((Course c) -> c.getTimeSlot().getDay().getValue())
            .thenComparing(c -> c.getTimeSlot().getStart()));
    return courses;
  }
}
