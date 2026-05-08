package vn.edu.studentmanagement.service;

import java.util.ArrayList;
import java.util.Comparator;
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

public class ScheduleService {
  private final StudentService studentService;
  private final StudentValidator studentValidator;
  private final StudentNormalizer studentNormalizer;
  private final CourseCatalog courseCatalog;
  private final ScheduleValidator scheduleValidator;
  private final ScheduleStore scheduleStore;

  private final Map<String, Schedule> schedulesByStudentId;

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
    this(studentService, new StudentNormalizer(), courseCatalog, scheduleRepository);
  }

  private ScheduleService(
      StudentService studentService,
      StudentNormalizer studentNormalizer,
      CourseCatalog courseCatalog,
      CsvRepository<Schedule> scheduleRepository) {
    this(
        studentService,
        new StudentValidator(studentNormalizer),
        studentNormalizer,
        courseCatalog,
        scheduleRepository);
  }

  public ScheduleService(
      StudentService studentService,
      StudentValidator studentValidator,
      StudentNormalizer studentNormalizer,
      CourseCatalog courseCatalog,
      CsvRepository<Schedule> scheduleRepository) {
    this.studentService = Objects.requireNonNull(studentService);
    this.studentValidator = Objects.requireNonNull(studentValidator);
    this.studentNormalizer = Objects.requireNonNull(studentNormalizer);
    this.courseCatalog = Objects.requireNonNull(courseCatalog);
    this.scheduleValidator = new ScheduleValidator(courseCatalog);
    this.scheduleStore = new ScheduleStore(Objects.requireNonNull(scheduleRepository), studentNormalizer);
    this.schedulesByStudentId = scheduleStore.loadSchedulesByStudentId();
  }

  public boolean overlap(TimeSlot a, TimeSlot b) {
    return scheduleValidator.overlap(a, b);
  }

  public AddCourseResult addCourse(String studentId, String courseId) {
    try {
      studentValidator.validateExistingStudentId(studentId);
      scheduleValidator.validateCourseId(courseId);

      String sid = studentNormalizer.normalizeStudentId(studentId);
      String cid = normalizeCourseId(courseId);

      Student student = studentService.filterById(sid);
      if (student == null) {
        throw new IllegalArgumentException("ID not found");
      }

      CourseDefinition def = courseCatalog.findByCourseId(cid);
      if (def == null) {
        throw new IllegalArgumentException("Course not found");
      }

      scheduleValidator.validateCourseAllowedForMajor(def, student.getMajor());

      Course selectedCourse = courseCatalog.createScheduledCourse(cid);
      Schedule schedule = schedulesByStudentId.computeIfAbsent(sid, Schedule::new);
      scheduleValidator.validateCourseCanBeAdded(schedule, cid, selectedCourse);

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
    studentValidator.validateExistingStudentId(studentId);
    if (courseId == null || courseId.isBlank()) {
      throw new IllegalArgumentException("Course id cannot be empty.");
    }
    String sid = studentNormalizer.normalizeStudentId(studentId);
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
    studentValidator.validateExistingStudentId(studentId);

    Schedule removedSchedule = schedulesByStudentId.remove(studentNormalizer.normalizeStudentId(studentId));
    if (removedSchedule == null) {
      return false;
    }

    markScheduleChanged();
    return true;
  }

  public void flushPendingChanges() {
    scheduleStore.flushPendingChanges(schedulesByStudentId);
  }

  public Schedule getSchedule(String studentId) {
    studentValidator.validateExistingStudentId(studentId);
    String sid = studentNormalizer.normalizeStudentId(studentId);
    Schedule schedule = schedulesByStudentId.get(sid);
    if (schedule == null) {
      schedule = new Schedule(sid);
    }
    return schedule;
  }

  public Schedule filterScheduleByStudentId(String studentId) {
    return getSchedule(studentId);
  }

  public List<Course> getScheduleSortedByDayThenStart(String studentId) {
    return filterScheduleByStudentIdSortedByDayThenStart(studentId);
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

  private void markScheduleChanged() {
    scheduleStore.markChanged(schedulesByStudentId);
  }
}
