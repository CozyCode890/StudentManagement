package vn.edu.studentmanagement.ui;

import java.util.List;
import java.util.Objects;

import vn.edu.studentmanagement.model.Course;
import vn.edu.studentmanagement.model.Student;
import vn.edu.studentmanagement.storage.CourseCatalog;
import vn.edu.studentmanagement.service.ScheduleService;
import vn.edu.studentmanagement.service.StudentService;

public class ScheduleMenu {
  private static StudentService studentService;
  private static CourseCatalog courseCatalog;
  private static ScheduleService scheduleService;

  public static void run(
      StudentService sharedStudentService,
      CourseCatalog sharedCourseCatalog,
      ScheduleService sharedScheduleService) {
    studentService = Objects.requireNonNull(sharedStudentService);
    courseCatalog = Objects.requireNonNull(sharedCourseCatalog);
    scheduleService = Objects.requireNonNull(sharedScheduleService);
    while (true) {
      TerminalController.clearScreen();

      System.out.println("\n==================================");
      System.out.println("       COURSE REGISTRATION        ");
      System.out.println("==================================");
      System.out.println("1) View Student Schedule");
      System.out.println("2) Add Course to Schedule");
      System.out.println("3) Remove Course from Schedule");
      System.out.println("0) Back to main menu");

      String choice = ConsolePrompt.trimmed("Choose: ");
      switch (choice) {
        case "1" -> {
          viewSchedule();
          ConsolePause.waitForEnter();
        }
        case "2" -> {
          addCourseToSchedule();
        }
        case "3" -> {
          removeCourseFromSchedule();
          ConsolePause.waitForEnter();
        }
        case "0" -> {
          if (flushPendingScheduleChanges()) {
            return;
          }
        }
        default -> {
          ConsoleMessagePrinter.warning("Invalid choice.");
          ConsolePause.waitForEnter();
        }
      }
    }
  }

  private static void viewSchedule() {
    Student student = askForStudent();
    if (student == null)
      return;

    try {
      List<Course> courses = scheduleService.getScheduleSortedByDayThenStart(student.getId());

      System.out.println("\n>>> SCHEDULE FOR: " + student.getFullName().toUpperCase() + " (ID: " + student.getId() + ")");
      if (courses.isEmpty()) {
        ConsoleMessagePrinter.warning("No courses registered yet");
      } else {
        CourseTableRenderer.renderCourses(courses);
      }
    } catch (IllegalArgumentException | IllegalStateException e) {
      ConsoleMessagePrinter.error(e);
    }
  }

  private static void addCourseToSchedule() {
    Student student = askForStudent();
    if (student == null) {
      ConsolePause.waitForEnter();
      return;
    }

    // 1. Hiển thị danh sách môn học có sẵn cho SV này
    try {
      System.out.println("\n--- AVAILABLE COURSES FOR " + student.getMajor() + " ---");
      System.out.println("\nGeneral courses:");
      CourseTableRenderer.renderDefinitions(courseCatalog.getGeneralCourses());
      System.out.println("\nMajor courses:");
      CourseTableRenderer.renderDefinitions(courseCatalog.getMajorCoursesByStudentMajor(student.getMajor()));

      while (true) {
        String courseId = ConsolePrompt.courseIdOrBack("\nEnter Course ID to add (B to back): ");
        if (courseId.equals("B")) {
          return;
        }

        ScheduleService.AddCourseResult result = scheduleService.addCourse(student.getId(), courseId);
        if (result.isSuccess()) {
          ConsoleMessagePrinter.success(result.getMessage());
          continue;
        }

        ConsoleMessagePrinter.error(result.getMessage());
      }
    } catch (IllegalArgumentException | IllegalStateException e) {
      ConsoleMessagePrinter.error(e);
      ConsolePause.waitForEnter();
    }
  }

  private static void removeCourseFromSchedule() {
    Student student = askForStudent();
    if (student == null)
      return;

    try {
      List<Course> currentCourses = scheduleService.getSchedule(student.getId()).getSelectedCourses();
      if (currentCourses.isEmpty()) {
        ConsoleMessagePrinter.warning("This student has no courses to remove.");
        return;
      }

      CourseTableRenderer.renderCourses(currentCourses);
      String courseId = ConsolePrompt.upperTrimmed("\nEnter Course ID to remove: ");

      boolean removed = scheduleService.removeCourse(student.getId(), courseId);
      if (removed) {
        ConsoleMessagePrinter.success("Course removed successfully.");
      } else {
        ConsoleMessagePrinter.error("Course ID not found in student's schedule.");
      }
    } catch (IllegalArgumentException | IllegalStateException e) {
      ConsoleMessagePrinter.error(e);
    }
  }

  // --- HELPER METHODS ---

  private static Student askForStudent() {
    String sid = ConsolePrompt.trimmed("Enter student ID: ");
    try {
      Student s = studentService.findById(sid);
      if (s == null) {
        ConsoleMessagePrinter.warning("Student not found with ID: " + sid);
      }
      return s;
    } catch (IllegalArgumentException | IllegalStateException e) {
      ConsoleMessagePrinter.error(e);
      return null;
    }
  }

  private static boolean flushPendingScheduleChanges() {
    try {
      scheduleService.flushPendingChanges();
      return true;
    } catch (IllegalStateException e) {
      ConsoleMessagePrinter.error(e);
      ConsolePause.waitForEnter();
      return false;
    }
  }

}
