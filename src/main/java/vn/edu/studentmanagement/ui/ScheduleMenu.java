package vn.edu.studentmanagement.ui;

import java.util.List;
import java.util.Objects;

import vn.edu.studentmanagement.model.Course;
import vn.edu.studentmanagement.model.CourseDefinition;
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
      ConsoleIO.clearScreen();

      System.out.println("\n==================================");
      System.out.println("       COURSE REGISTRATION        ");
      System.out.println("==================================");
      System.out.println("1) View Student Schedule");
      System.out.println("2) Add Course to Schedule");
      System.out.println("3) Remove Course from Schedule");
      System.out.println("0) Back to main menu");

      String choice = ConsoleIO.promptTrimmed("Choose: ");
      switch (choice) {
        case "1" -> {
          viewSchedule();
          ConsoleIO.pause();
        }
        case "2" -> {
          addCourseToSchedule();
          ConsoleIO.pause();
        }
        case "3" -> {
          removeCourseFromSchedule();
          ConsoleIO.pause();
        }
        case "0" -> {
          if (flushPendingScheduleChanges()) {
            return;
          }
        }
        default -> {
          ConsoleIO.printWarning("Invalid choice.");
          ConsoleIO.pause();
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
        ConsoleIO.printWarning("No courses registered yet");
      } else {
        renderCourseTable(courses);
      }
    } catch (IllegalArgumentException | IllegalStateException e) {
      ConsoleIO.printError(e);
    }
  }

  private static void addCourseToSchedule() {
    Student student = askForStudent();
    if (student == null)
      return;

    // 1. Hiển thị danh sách môn học có sẵn cho SV này
    try {
      System.out.println("\n--- AVAILABLE COURSES FOR " + student.getMajor() + " ---");
      System.out.println("\nGeneral courses:");
      renderDefinitionTable(courseCatalog.getGeneralCourses());
      System.out.println("\nMajor courses:");
      renderDefinitionTable(courseCatalog.getMajorCoursesByStudentMajor(student.getMajor()));

      while (true) {
        String courseId = ConsoleIO.promptUpperTrimmed("\nEnter Course ID to add (B to back): ");
        if (courseId.equals("B")) {
          return;
        }

        ScheduleService.AddCourseResult result = scheduleService.addCourse(student.getId(), courseId);
        if (result.isSuccess()) {
          ConsoleIO.printSuccess(result.getMessage());
          return;
        }

        ConsoleIO.printError(result.getMessage());
      }
    } catch (IllegalArgumentException | IllegalStateException e) {
      ConsoleIO.printError(e);
    }
  }

  private static void removeCourseFromSchedule() {
    Student student = askForStudent();
    if (student == null)
      return;

    try {
      List<Course> currentCourses = scheduleService.getSchedule(student.getId()).getSelectedCourses();
      if (currentCourses.isEmpty()) {
        ConsoleIO.printWarning("This student has no courses to remove.");
        return;
      }

      renderCourseTable(currentCourses);
      String courseId = ConsoleIO.promptUpperTrimmed("\nEnter Course ID to remove: ");

      boolean removed = scheduleService.removeCourse(student.getId(), courseId);
      if (removed) {
        ConsoleIO.printSuccess("Course removed successfully.");
      } else {
        ConsoleIO.printError("Course ID not found in student's schedule.");
      }
    } catch (IllegalArgumentException | IllegalStateException e) {
      ConsoleIO.printError(e);
    }
  }

  // --- HELPER METHODS ---

  private static Student askForStudent() {
    String sid = ConsoleIO.promptTrimmed("Enter student ID: ");
    try {
      Student s = studentService.findById(sid);
      if (s == null) {
        ConsoleIO.printWarning("Student not found with ID: " + sid);
      }
      return s;
    } catch (IllegalArgumentException | IllegalStateException e) {
      ConsoleIO.printError(e);
      return null;
    }
  }

  private static boolean flushPendingScheduleChanges() {
    try {
      scheduleService.flushPendingChanges();
      return true;
    } catch (IllegalStateException e) {
      ConsoleIO.printError(e);
      ConsoleIO.pause();
      return false;
    }
  }

  private static void renderCourseTable(List<Course> courses) {
    String format = "| %-8s | %-20s | %-10s | %-15s |%n";
    String line = ConsoleIO.buildSeparator(8, 20, 10, 15);
    System.out.println(line);
    System.out.printf(format, "ID", "Course Name", "Day", "Time");
    System.out.println(line);
    for (Course c : courses) {
      String timeStr = c.getTimeSlot().getStart() + "-" + c.getTimeSlot().getEnd();
      System.out.printf(format,
          c.getCourseId(),
          c.getName(),
          c.getTimeSlot().getDay(),
          timeStr);
    }
    System.out.println(line);
  }

  private static void renderDefinitionTable(List<CourseDefinition> defs) {
    String format = "| %-8s | %-25s | %-10s |%n";
    String line = ConsoleIO.buildSeparator(8, 25, 10);
    System.out.println(line);
    System.out.printf(format, "ID", "Course Name", "Type");
    System.out.println(line);
    for (CourseDefinition d : defs) {
      System.out.printf(format, d.getCourseId(), d.getName(), d.getType());
    }
    System.out.println(line);
  }
}
