package vn.edu.studentmanagement.ui;

import java.util.List;
import java.util.Objects;

import vn.edu.studentmanagement.model.Student;
import vn.edu.studentmanagement.service.ScheduleService;
import vn.edu.studentmanagement.service.StudentService;

public class StudentMenu {
  private static StudentService studentService;
  private static ScheduleService scheduleService;
  private static StudentListView studentListView;

  public static void run(StudentService sharedStudentService, ScheduleService sharedScheduleService) {
    studentService = Objects.requireNonNull(sharedStudentService);
    scheduleService = Objects.requireNonNull(sharedScheduleService);
    studentListView = new StudentListView(studentService);
    while (true) {
      TerminalController.clearScreen();

      System.out.println("\n=== STUDENT MANAGEMENT SYSTEM ===");
      System.out.println("1) View Student Menu (Search/List)");
      System.out.println("2) Add New Student");
      System.out.println("3) Delete Student (by ID)");
      System.out.println("0) Back to main menu");

      String choice = ConsolePrompt.trimmed("Choose: ");
      switch (choice) {
        case "1" -> studentListView.show();
        case "2" -> {
          addStudent();
          ConsolePause.waitForEnter();
        }
        case "3" -> {
          deleteStudentById();
          ConsolePause.waitForEnter();
        }
        case "0" -> {
          if (flushPendingStudentChanges()) {
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

  public static void viewStudentsPaginated(List<Student> allStudents) {
    ConsolePaginator.show(
        allStudents,
        10,
        "Student list is empty.",
        StudentTableRenderer::render);
  }

  public static void addStudent() {
    String id = ConsoleIO.prompt("\nEnter ID: ");
    String name = ConsoleIO.prompt("\nEnter name: ");
    String gender = ConsoleIO.prompt("Enter gender (Male/Female/M/F): ");

    try {
      Student student = studentService.addStudent(id, name, gender);
      System.out.println("Successfully added ID: " + student.getId());
    } catch (IllegalArgumentException | IllegalStateException e) {
      ConsoleMessagePrinter.error(e);
    }
  }

  public static void deleteStudentById() {
    String input = ConsolePrompt.trimmed("\nEnter ID to delete: ");

    try {
      Student deletedStudent = studentService.deleteStudentById(input);
      boolean removedSchedule = scheduleService.removeScheduleByStudentId(input);
      System.out.println("Student with ID " + deletedStudent.getId() + " has been deleted.");
      if (removedSchedule) {
        System.out.println("Schedule for student ID " + input + " has also been removed.");
      }
    } catch (IllegalArgumentException | IllegalStateException e) {
      ConsoleMessagePrinter.error(e);
    }
  }

  private static boolean flushPendingStudentChanges() {
    try {
      studentService.flushPendingChanges();
      scheduleService.flushPendingChanges();
      return true;
    } catch (IllegalStateException e) {
      ConsoleMessagePrinter.error(e);
      ConsolePause.waitForEnter();
      return false;
    }
  }
}
