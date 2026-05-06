package vn.edu.studentmanagement.ui;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import vn.edu.studentmanagement.model.Course;
import vn.edu.studentmanagement.model.CourseDefinition;
import vn.edu.studentmanagement.model.Student;
import vn.edu.studentmanagement.storage.CourseCatalog;
import vn.edu.studentmanagement.service.ScheduleService;
import vn.edu.studentmanagement.service.StudentService;

public class ScheduleMenu {
  private static final Scanner SC = new Scanner(System.in, StandardCharsets.UTF_8);

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
      System.out.print("Choose: ");

      String choice = SC.nextLine().trim();
      switch (choice) {
        case "1" -> {
          viewSchedule();
          pause();
        }
        case "2" -> {
          addCourseToSchedule();
          pause();
        }
        case "3" -> {
          removeCourseFromSchedule();
          pause();
        }
        case "0" -> {
          scheduleService.flushPendingChanges();
          return;
        }
        default -> {
          System.out.println("[!] Invalid choice.");
          pause();
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
        System.out.println("(No courses registered yet)");
      } else {
        renderCourseTable(courses);
      }
    } catch (IllegalArgumentException | IllegalStateException e) {
      printError(e);
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

      // 2. Nhập mã môn học
      System.out.print("\nEnter Course ID to add: ");
      String courseId = SC.nextLine().trim().toUpperCase();

      // 3. Gọi service xử lý logic (check trùng, check conflict, check max 3 môn)
      ScheduleService.AddCourseResult result = scheduleService.addCourse(student.getId(), courseId);

      if (result.isSuccess()) {
        System.out.println("[OK] " + result.getMessage());
      } else {
        System.out.println("[ERROR] " + result.getMessage());
      }
    } catch (IllegalArgumentException | IllegalStateException e) {
      printError(e);
    }
  }

  private static void removeCourseFromSchedule() {
    Student student = askForStudent();
    if (student == null)
      return;

    try {
      List<Course> currentCourses = scheduleService.getSchedule(student.getId()).getSelectedCourses();
      if (currentCourses.isEmpty()) {
        System.out.println("[!] This student has no courses to remove.");
        return;
      }

      renderCourseTable(currentCourses);
      System.out.print("\nEnter Course ID to remove: ");
      String courseId = SC.nextLine().trim().toUpperCase();

      boolean removed = scheduleService.removeCourse(student.getId(), courseId);
      if (removed) {
        System.out.println("[OK] Course removed successfully.");
      } else {
        System.out.println("[ERROR] Course ID not found in student's schedule.");
      }
    } catch (IllegalArgumentException | IllegalStateException e) {
      printError(e);
    }
  }

  // --- HELPER METHODS ---

  private static Student askForStudent() {
    System.out.print("Enter student ID: ");
    String sid = SC.nextLine().trim();
    try {
      Student s = studentService.findById(sid);
      if (s == null) {
        System.out.println("[!] Student not found with ID: " + sid);
      }
      return s;
    } catch (IllegalArgumentException | IllegalStateException e) {
      printError(e);
      return null;
    }
  }

  private static void printError(RuntimeException e) {
    System.out.println("[ERROR] " + e.getMessage());
  }

  private static void pause() {
    System.out.print("\nPress Enter to continue...");
    SC.nextLine();
  }

  private static void renderCourseTable(List<Course> courses) {
    String format = "| %-8s | %-20s | %-10s | %-15s |%n";
    String line = "+----------+----------------------+------------+-----------------+";
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
    String line = "+----------+---------------------------+------------+";
    System.out.println(line);
    System.out.printf(format, "ID", "Course Name", "Type");
    System.out.println(line);
    for (CourseDefinition d : defs) {
      System.out.printf(format, d.getCourseId(), d.getName(), d.getType());
    }
    System.out.println(line);
  }
}
