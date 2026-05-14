package vn.edu.studentmanagement.application.schedule;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import vn.edu.studentmanagement.application.store.Repository;
import vn.edu.studentmanagement.application.student.StudentService;
import vn.edu.studentmanagement.domain.catalog.CourseCatalog;
import vn.edu.studentmanagement.domain.model.Course;
import vn.edu.studentmanagement.domain.model.Schedule;
import vn.edu.studentmanagement.domain.model.Student;
import vn.edu.studentmanagement.domain.normalization.CourseNormalizer;
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
  private final CourseNormalizer courseNormalizer;

  private final Map<String, Schedule> schedulesByStudentId;

  public static class AvailableCourses {
    private final List<Course> generalCourses;
    private final List<Course> majorCourses;

    private AvailableCourses(List<Course> generalCourses, List<Course> majorCourses) {
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
    this.courseNormalizer = new CourseNormalizer();
  }

  public Course addCourse(String studentId, String courseId) {
    studentValidator.validateRequiredStudentId(studentId);
    scheduleValidator.validateCourseId(courseId);

    String sid = studentNormalizer.normalizeStudentId(studentId);
    String cid = courseNormalizer.normalizeCourseId(courseId);

    Student student = studentService.findRequiredById(sid);
    Course selectedCourse = courseCatalog.findByCourseId(cid);
    if (selectedCourse == null) {
      throw new IllegalArgumentException("Course not found: " + cid);
    }

    scheduleValidator.validateCourseAllowedForMajor(selectedCourse, student.getMajor());

    Schedule schedule = schedulesByStudentId.computeIfAbsent(sid, Schedule::new);
    scheduleValidator.validateCourseCanBeAdded(schedule, cid, selectedCourse);

    schedule.addCourse(selectedCourse);
    markScheduleChanged();
    return selectedCourse;
  }

  public boolean removeCourse(String studentId, String courseId) {
    Student student = studentService.findRequiredById(studentId);
    if (courseId == null || courseId.isBlank()) {
      throw new IllegalArgumentException("Course id cannot be empty.");
    }
    String sid = student.getId();
    String cid = courseNormalizer.normalizeCourseId(courseId);

    Schedule schedule = schedulesByStudentId.get(sid);
    if (schedule == null)
      return false;

    boolean removed = schedule.removeCourseById(cid);
    if (removed) {
      if (!schedule.hasSelectedCourses()) {
        schedulesByStudentId.remove(sid);
      }
      markScheduleChanged();
    }
    return removed;
  }

  public boolean removeScheduleByStudentId(String studentId) {
    Student student = studentService.findRequiredById(studentId);

    Schedule removedSchedule = schedulesByStudentId.remove(student.getId());
    if (removedSchedule == null) {
      return false;
    }

    markScheduleChanged();
    return true;
  }

  public AvailableCourses getAvailableCoursesForStudent(String studentId) {
    Student student = studentService.findRequiredById(studentId);

    return new AvailableCourses(
        courseCatalog.getGeneralCourses(),
        courseCatalog.getMajorCoursesByStudentMajor(student.getMajor()));
  }

  public void flushPendingChanges() {
    scheduleStore.flushPendingChanges(schedulesByStudentId);
  }

  public List<Course> findCoursesByStudentId(String studentId) {
    Student student = studentService.findRequiredById(studentId);
    String sid = student.getId();
    Schedule schedule = schedulesByStudentId.get(sid);
    if (schedule == null) {
      schedule = new Schedule(sid);
    }
    return schedule.getSelectedCourses();
  }

  public boolean hasReachedCourseLimit(String studentId) {
    Student student = studentService.findRequiredById(studentId);
    Schedule schedule = schedulesByStudentId.get(student.getId());
    return schedule != null && schedule.isFull();
  }

  public List<Course> getScheduleSortedByDayThenStart(String studentId) {
    List<Course> courses = new ArrayList<>(findCoursesByStudentId(studentId));
    courses.sort(
        Comparator.comparing((Course c) -> c.getTimeSlot().getDay().getValue())
            .thenComparing(c -> c.getTimeSlot().getStart()));
    return courses;
  }

  private void markScheduleChanged() {
    scheduleStore.markChanged(schedulesByStudentId);
  }
}
