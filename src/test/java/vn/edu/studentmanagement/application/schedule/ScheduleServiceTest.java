package vn.edu.studentmanagement.application.schedule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import vn.edu.studentmanagement.application.student.StudentService;
import vn.edu.studentmanagement.domain.catalog.CourseCatalog;
import vn.edu.studentmanagement.domain.model.Course;
import vn.edu.studentmanagement.domain.model.Schedule;
import vn.edu.studentmanagement.domain.model.Student;
import vn.edu.studentmanagement.support.InMemoryRepository;

class ScheduleServiceTest {
  @Test
  void addCourseReturnsAddedCourseAndThrowsForInvalidOperations() {
    StudentService studentService = new StudentService(new InMemoryRepository<>());
    studentService.addStudent("ITITIU21001", "Nguyen Van A", "M");
    CourseCatalog courseCatalog = new CourseCatalog();
    ScheduleService scheduleService = new ScheduleService(
        studentService,
        courseCatalog,
        new InMemoryRepository<Schedule>());

    Course addedCourse = scheduleService.addCourse("ititiu21001", "it201");

    assertEquals("IT201", addedCourse.getCourseId());
    assertEquals(1, scheduleService.findCoursesByStudentId("ITITIU21001").size());

    IllegalArgumentException duplicateException = assertThrows(
        IllegalArgumentException.class,
        () -> scheduleService.addCourse("ITITIU21001", "IT201"));
    assertEquals("Course already added", duplicateException.getMessage());

    IllegalArgumentException missingStudentException = assertThrows(
        IllegalArgumentException.class,
        () -> scheduleService.addCourse("ITITIU21002", "IT202"));
    assertEquals("ID not found: ITITIU21002", missingStudentException.getMessage());
  }

  @Test
  void scheduleOperationsRequireExistingStudent() {
    StudentService studentService = new StudentService(new InMemoryRepository<Student>());
    CourseCatalog courseCatalog = new CourseCatalog();
    ScheduleService scheduleService = new ScheduleService(
        studentService,
        courseCatalog,
        new InMemoryRepository<Schedule>());

    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> scheduleService.findCoursesByStudentId("ITITIU21001"));

    assertEquals("ID not found: ITITIU21001", exception.getMessage());
  }
}
