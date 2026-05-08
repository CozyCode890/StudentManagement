package vn.edu.studentmanagement.bootstrap;

import vn.edu.studentmanagement.application.schedule.ScheduleService;
import vn.edu.studentmanagement.application.student.StudentService;
import vn.edu.studentmanagement.domain.catalog.CourseCatalog;
import vn.edu.studentmanagement.infrastructure.csv.CsvScheduleRepository;
import vn.edu.studentmanagement.infrastructure.csv.CsvStudentRepository;
import vn.edu.studentmanagement.presentation.console.controller.MainController;

public class AppConfig {
  public MainController mainController() {
    CsvStudentRepository studentRepository = new CsvStudentRepository();
    CourseCatalog courseCatalog = new CourseCatalog();
    CsvScheduleRepository scheduleRepository = new CsvScheduleRepository(courseCatalog);
    studentRepository.ensureFileExists();
    scheduleRepository.ensureFileExists();

    StudentService studentService = new StudentService(studentRepository);
    ScheduleService scheduleService = new ScheduleService(studentService, courseCatalog, scheduleRepository);

    return new MainController(studentService, scheduleService, courseCatalog);
  }
}
