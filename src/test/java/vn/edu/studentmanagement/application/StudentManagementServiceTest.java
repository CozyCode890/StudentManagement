package vn.edu.studentmanagement.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import vn.edu.studentmanagement.application.schedule.ScheduleService;
import vn.edu.studentmanagement.application.student.StudentService;
import vn.edu.studentmanagement.domain.catalog.CourseCatalog;
import vn.edu.studentmanagement.domain.model.Schedule;
import vn.edu.studentmanagement.domain.model.Student;
import vn.edu.studentmanagement.support.InMemoryRepository;

class StudentManagementServiceTest {
  @Test
  void deleteStudentByIdAlsoRemovesStudentSchedule() {
    InMemoryRepository<Student> studentRepository = new InMemoryRepository<>();
    InMemoryRepository<Schedule> scheduleRepository = new InMemoryRepository<>();
    StudentService studentService = new StudentService(studentRepository);
    CourseCatalog courseCatalog = new CourseCatalog();
    ScheduleService scheduleService = new ScheduleService(studentService, courseCatalog, scheduleRepository);
    StudentManagementService managementService = new StudentManagementService(studentService, scheduleService);

    studentService.addStudent("ITITIU21001", "Nguyen Van A", "M");
    scheduleService.addCourse("ITITIU21001", "IT201");

    StudentManagementService.DeleteStudentResult result = managementService.deleteStudentById("ititiu21001");
    managementService.flushPendingChanges();

    assertEquals("ITITIU21001", result.getStudent().getId());
    assertTrue(result.isScheduleRemoved());
    assertTrue(studentRepository.getLastWrittenItems().isEmpty());
    assertTrue(scheduleRepository.getLastWrittenItems().isEmpty());
  }
}
