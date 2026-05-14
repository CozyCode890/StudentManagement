package vn.edu.studentmanagement.application;

import java.util.Objects;

import vn.edu.studentmanagement.application.schedule.ScheduleService;
import vn.edu.studentmanagement.application.student.StudentService;
import vn.edu.studentmanagement.domain.model.Student;

public class StudentManagementService {
  private final StudentService studentService;
  private final ScheduleService scheduleService;

  public StudentManagementService(
      StudentService studentService,
      ScheduleService scheduleService) {
    this.studentService = Objects.requireNonNull(studentService);
    this.scheduleService = Objects.requireNonNull(scheduleService);
  }

  public DeleteStudentResult deleteStudentById(String studentId) {
    Student deletedStudent = studentService.deleteStudentById(studentId);
    boolean removedSchedule = scheduleService.removeScheduleByStudentId(deletedStudent.getId());

    return new DeleteStudentResult(deletedStudent, removedSchedule);
  }

  public void flushPendingChanges() {
    studentService.flushPendingChanges();
    scheduleService.flushPendingChanges();
  }

  public static class DeleteStudentResult {
    private final Student student;
    private final boolean scheduleRemoved;

    public DeleteStudentResult(Student student, boolean scheduleRemoved) {
      this.student = Objects.requireNonNull(student);
      this.scheduleRemoved = scheduleRemoved;
    }

    public Student getStudent() {
      return student;
    }

    public boolean isScheduleRemoved() {
      return scheduleRemoved;
    }
  }
}
