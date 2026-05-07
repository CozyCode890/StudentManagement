package vn.edu.studentmanagement.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import vn.edu.studentmanagement.model.Course;
import vn.edu.studentmanagement.model.CourseDefinition;
import vn.edu.studentmanagement.model.Schedule;
import vn.edu.studentmanagement.model.Student;
import vn.edu.studentmanagement.model.TimeSlot;
import vn.edu.studentmanagement.storage.CourseCatalog;
import vn.edu.studentmanagement.storage.CsvRepository;
import vn.edu.studentmanagement.storage.CsvScheduleRepository;
import vn.edu.studentmanagement.storage.StorageException;

public class ScheduleService {
  private static final int SAVE_BATCH_SIZE = 5;

  private final StudentService studentService;
  private final CourseCatalog courseCatalog;
  private final CsvRepository<Schedule> scheduleRepository;

  private final Map<String, Schedule> schedulesByStudentId = new HashMap<>();
  private int pendingScheduleChanges;

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
    this(studentService, courseCatalog, new CsvScheduleRepository(courseCatalog));
  }

  public ScheduleService(
      StudentService studentService,
      CourseCatalog courseCatalog,
      CsvRepository<Schedule> scheduleRepository) {
    this.studentService = Objects.requireNonNull(studentService);
    this.courseCatalog = Objects.requireNonNull(courseCatalog);
    this.scheduleRepository = Objects.requireNonNull(scheduleRepository);
    loadSchedules();
  }


  public boolean overlap(TimeSlot a, TimeSlot b) {
    if (a == null || b == null)
      return false;
    if (a.getDay() != b.getDay())
      return false;
    return a.getStart().compareTo(b.getEnd()) < 0 && b.getStart().compareTo(a.getEnd()) < 0;
  }

  public AddCourseResult addCourse(String studentId, String courseId) {
    try {
      if (studentId == null || studentId.isBlank()) {
        throw new IllegalArgumentException("ID cannot be empty.");
      }
      if (courseId == null || courseId.isBlank()) {
        throw new IllegalArgumentException("Course ID cannot be empty.");
      }

      String sid = studentId.trim();
      String cid = normalizeCourseId(courseId);

      Student student = studentService.filterById(sid);
      if (student == null) {
        throw new IllegalArgumentException("ID not found");
      }

      CourseDefinition def = courseCatalog.findByCourseId(cid);
      if (def == null) {
        throw new IllegalArgumentException("Course not found");
      }

      if (!courseCatalog.isEligibleForMajor(def, student.getMajor())) {
        throw new IllegalArgumentException("Course not allowed for student's major");
      }

      Course selectedCourse = courseCatalog.createScheduledCourse(cid);
      TimeSlot proposedTime = selectedCourse.getTimeSlot();

      Schedule schedule = schedulesByStudentId.computeIfAbsent(sid, Schedule::new);

      for (Course selected : schedule.getSelectedCourses()) {
        if (selected.getCourseId().equals(cid)) {
          throw new IllegalArgumentException("Course already added");
        }
      }

      if (schedule.selectedCoursesCount() >= 3) {
        throw new IllegalArgumentException("Max 3 courses");
      }

      for (Course selected : schedule.getSelectedCourses()) {
        if (overlap(selected.getTimeSlot(), proposedTime)) {
          throw new IllegalArgumentException("Conflict time");
        }
      }

      if (!isValidTimeSlot(proposedTime)) {
        throw new IllegalArgumentException("Course scheduled outside valid time slots");
      }

      schedule.getSelectedCourses().add(selectedCourse);
      markScheduleChanged();
      return new AddCourseResult(true, "Added successfully");
    } catch (IllegalArgumentException e) {
      return new AddCourseResult(false, e.getMessage());
    } catch (IllegalStateException e) {
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
    String cid = normalizeCourseId(courseId);

    Schedule schedule = schedulesByStudentId.get(sid);
    if (schedule == null)
      return false;

    boolean removed = schedule.getSelectedCourses().removeIf(c -> c.getCourseId().equals(cid));
    if (schedule.getSelectedCourses().isEmpty()) {
      schedulesByStudentId.remove(sid);
    }
    if (removed) {
      markScheduleChanged();
    }
    return removed;
  }

  public boolean removeScheduleByStudentId(String studentId) {
    if (studentId == null || studentId.isBlank()) {
      throw new IllegalArgumentException("ID cannot be empty.");
    }

    Schedule removedSchedule = schedulesByStudentId.remove(studentId.trim());
    if (removedSchedule == null) {
      return false;
    }

    markScheduleChanged();
    return true;
  }

  public void flushPendingChanges() {
    if (pendingScheduleChanges > 0) {
      saveSchedules();
    }
  }

  public Schedule filterScheduleByStudentId(String studentId) {
    if (studentId == null || studentId.isBlank()) {
      throw new IllegalArgumentException("ID cannot be empty.");
    }
    String sid = studentId.trim();
    Schedule schedule = schedulesByStudentId.get(sid);
    if (schedule == null) {
      schedule = new Schedule(sid);
    }
    return schedule;
  }

  public List<Course> filterScheduleByStudentIdSortedByDayThenStart(String studentId) {
    List<Course> courses = new ArrayList<>(filterScheduleByStudentId(studentId).getSelectedCourses());
    courses.sort(
        Comparator.comparing((Course c) -> c.getTimeSlot().getDay().getValue())
            .thenComparing(c -> c.getTimeSlot().getStart()));
    return courses;
  }

  private String normalizeCourseId(String courseId) {
    return courseId.trim().toUpperCase(Locale.ROOT);
  }

  private boolean isValidTimeSlot(TimeSlot timeSlot) {
    return timeSlot != null && courseCatalog.getValidTimeSlots().contains(timeSlot);
  }

  private void loadSchedules() {
    try {
      for (Schedule schedule : scheduleRepository.readAll()) {
        if (schedule.getStudentId() != null && !schedule.getStudentId().isBlank()) {
          schedulesByStudentId.put(schedule.getStudentId().trim(), schedule);
        }
      }
    } catch (StorageException e) {
      throw new IllegalStateException("Unable to load schedules: " + getCauseMessage(e), e);
    }
  }

  private void saveSchedules() {
    try {
      scheduleRepository.writeAll(new ArrayList<>(schedulesByStudentId.values()));
      pendingScheduleChanges = 0;
    } catch (StorageException e) {
      throw new IllegalStateException("Unable to save schedules: " + getCauseMessage(e), e);
    }
  }

  private void markScheduleChanged() {
    pendingScheduleChanges++;
    if (pendingScheduleChanges >= SAVE_BATCH_SIZE) {
      saveSchedules();
    }
  }

  private String getCauseMessage(StorageException e) {
    Throwable cause = e.getCause();
    return cause != null && cause.getMessage() != null ? cause.getMessage() : e.getMessage();
  }
}
