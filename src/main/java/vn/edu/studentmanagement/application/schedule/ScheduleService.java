package vn.edu.studentmanagement.application.schedule;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import vn.edu.studentmanagement.application.student.StudentService;
import vn.edu.studentmanagement.application.store.Repository;
import vn.edu.studentmanagement.domain.model.Course;
import vn.edu.studentmanagement.domain.model.Schedule;
import vn.edu.studentmanagement.domain.model.Student;
import vn.edu.studentmanagement.domain.model.TimeSlot;
import vn.edu.studentmanagement.domain.catalog.CourseCatalog;
import vn.edu.studentmanagement.domain.normalization.StudentNormalizer;
import vn.edu.studentmanagement.domain.validation.ScheduleValidator;
import vn.edu.studentmanagement.domain.validation.StudentValidator;

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

  public static class AvailableCourses {
    private final List<Course> generalCourses;
    private final List<Course> majorCourses;

    public AvailableCourses(List<Course> generalCourses, List<Course> majorCourses) {
      this.generalCourses = List.copyOf(generalCourses);
      this.majorCourses = List.copyOf(majorCourses);
    }

    public List<Course> getGeneralCourses() {
      return generalCourses;
    }

    public List<Course> getMajorCourses() {
      return majorCourses;
    }
  }

  public ScheduleService(
      StudentService studentService,
      CourseCatalog courseCatalog,
      Repository<Schedule> scheduleRepository) {
    this(studentService, new StudentNormalizer(), courseCatalog, scheduleRepository);
  }

  private ScheduleService(
      StudentService studentService,
      StudentNormalizer studentNormalizer,
      CourseCatalog courseCatalog,
      Repository<Schedule> scheduleRepository) {
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
      Repository<Schedule> scheduleRepository) {
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

      Course selectedCourse = courseCatalog.findByCourseId(cid);
      if (selectedCourse == null) {
        throw new IllegalArgumentException("Course not found");
      }

      scheduleValidator.validateCourseAllowedForMajor(selectedCourse, student.getMajor());

      Schedule schedule = schedulesByStudentId.computeIfAbsent(sid, Schedule::new);
      scheduleValidator.validateCourseCanBeAdded(schedule, cid, selectedCourse);

      schedule.addCourse(selectedCourse);
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

    boolean removed = schedule.removeCourseById(cid);
    if (!schedule.hasSelectedCourses()) {
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

  public AvailableCourses getAvailableCoursesForStudent(String studentId) {
    studentValidator.validateExistingStudentId(studentId);

    Student student = studentService.findById(studentId);
    if (student == null) {
      throw new IllegalArgumentException("ID not found");
    }

    return new AvailableCourses(
        courseCatalog.getGeneralCourses(),
        courseCatalog.getMajorCoursesByStudentMajor(student.getMajor()));
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
