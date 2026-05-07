package vn.edu.studentmanagement.ui;

import java.util.List;
import java.util.Objects;

import vn.edu.studentmanagement.model.Student;
import vn.edu.studentmanagement.service.ScheduleService;
import vn.edu.studentmanagement.service.StudentService;

public class StudentMenu {
  private static StudentService studentService;
  private static ScheduleService scheduleService;

  public static void run(StudentService sharedStudentService, ScheduleService sharedScheduleService) {
    studentService = Objects.requireNonNull(sharedStudentService);
    scheduleService = Objects.requireNonNull(sharedScheduleService);
    while (true) {
      ConsoleIO.clearScreen();

      System.out.println("\n=== STUDENT MANAGEMENT SYSTEM ===");
      System.out.println("1) View Student Menu (Search/List)");
      System.out.println("2) Add New Student");
      System.out.println("3) Delete Student (by ID)");
      System.out.println("0) Back to main menu");

      String choice = ConsoleIO.promptTrimmed("Choose: ");
      switch (choice) {
        case "1" -> viewStudentMenu();
        case "2" -> {
          addStudent();
          ConsoleIO.pause();
        }
        case "3" -> {
          deleteStudentById();
          ConsoleIO.pause();
        }
        case "0" -> {
          if (flushPendingStudentChanges()) {
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

  private static void viewStudentMenu() {
    while (true) {
      ConsoleIO.clearScreen();

      System.out.println("\n--- VIEW OPTIONS ---");
      System.out.println("1) Show All Students");
      System.out.println("2) Search by Name");
      System.out.println("0) Return");

      String choice = ConsoleIO.promptTrimmed("Choice: ");
      if (choice.equals("0"))
        break;

      List<Student> students = studentService.findAll();
      String emptyMessage = "Student list is empty.";
      if (choice.equals("1")) {
        viewStudentsPaginated(students, emptyMessage);
      } else if (choice.equals("2")) {
        String keyword = ConsoleIO.prompt("Enter name keyword: ").toLowerCase();
        students = students.stream()
            .filter(s -> s.getFullName().toLowerCase().contains(keyword))
            .toList();
        emptyMessage = "No students matched your search.";
        viewStudentsPaginated(students, emptyMessage);
      } else {
        ConsoleIO.clearScreen();
        ConsoleIO.printWarning("Invalid choice.");
        ConsoleIO.pause();
      }
    }
  }

  public static void viewStudentsPaginated(List<Student> allStudents) {
    viewStudentsPaginated(allStudents, "Student list is empty.");
  }

  private static void viewStudentsPaginated(List<Student> allStudents, String emptyMessage) {
    int ROWS_PER_PAGE = 10;

    if (allStudents.isEmpty()) {
      System.out.println();
      ConsoleIO.printWarning(emptyMessage);
      ConsoleIO.pause();
      return;
    }

    int totalStudents = allStudents.size();
    int totalPages = (int) Math.ceil((double) totalStudents / ROWS_PER_PAGE);
    int currentPage = 0;
    String feedback = null;

    while (true) {
      ConsoleIO.clearScreen();
      int start = currentPage * ROWS_PER_PAGE;
      int end = Math.min(start + ROWS_PER_PAGE, totalStudents);

      List<Student> pageSlice = allStudents.subList(start, end);

      if (feedback != null) {
        ConsoleIO.printWarning(feedback);
        feedback = null;
      }

      System.out.println("\n--- Viewing Page " + (currentPage + 1) + " of " + totalPages + " ---");

      // Pass "start + 1" so STT shows correctly (e.g., 11, 12, 13... on page 2)
      renderTable(pageSlice, start + 1);

      System.out.println("\n[N] Next | [P] Previous | [B] Back");
      String choice = ConsoleIO.promptUpperTrimmed("Action: ");

      if (choice.equals("N") && currentPage < totalPages - 1) {
        currentPage++;
      } else if (choice.equals("P") && currentPage > 0) {
        currentPage--;
      } else if (choice.equals("B")) {
        break;
      } else {
        feedback = "Invalid choice or no more pages.";
      }
    }
  }

  private static void renderTable(List<Student> students, int startStt) {
    int sttWidth = Math.max(3, String.valueOf(startStt + students.size() - 1).length());
    int idWidth = Math.max("ID".length(), maxLength(students, s -> s.getId()));
    int nameWidth = Math.max("Full Name".length(), maxLength(students, s -> s.getFullName()));
    int genderWidth = Math.max("Gender".length(), maxLength(students, s -> String.valueOf(s.getGender())));
    int majorWidth = Math.max("Major".length(), maxLength(students, s -> String.valueOf(s.getMajor())));

    String format = "| %-" + sttWidth + "s | %-" + idWidth + "s | %-" + nameWidth + "s | %-"
        + genderWidth + "s | %-" + majorWidth + "s |%n";
    String line = ConsoleIO.buildSeparator(sttWidth, idWidth, nameWidth, genderWidth, majorWidth);

    System.out.println(line);
    System.out.printf(format, "STT", "ID", "Full Name", "Gender", "Major");
    System.out.println(line);

    int currentStt = startStt;
    for (Student s : students) {
      System.out.printf(format,
          currentStt++,
          ConsoleIO.safeText(s.getId()),
          ConsoleIO.safeText(s.getFullName()),
          ConsoleIO.safeText(String.valueOf(s.getGender())),
          ConsoleIO.safeText(String.valueOf(s.getMajor())));
    }
    System.out.println(line);
  }

  private static int maxLength(List<Student> students, java.util.function.Function<Student, String> valueExtractor) {
    return students.stream()
        .map(valueExtractor)
        .map(ConsoleIO::safeText)
        .mapToInt(String::length)
        .max()
        .orElse(0);
  }

  public static void addStudent() {
    String id = ConsoleIO.prompt("\nEnter ID: ");
    String name = ConsoleIO.prompt("\nEnter name: ");
    String major = ConsoleIO.prompt("Enter major: ");
    String gender = ConsoleIO.prompt("Enter gender (Male/Female/M/F): ");

    try {
      Student student = studentService.addStudent(id, name, major, gender);
      System.out.println("Successfully added ID: " + student.getId());
    } catch (IllegalArgumentException | IllegalStateException e) {
      ConsoleIO.printError(e);
    }
  }

  public static void deleteStudentById() {
    String input = ConsoleIO.promptTrimmed("\nEnter ID to delete: ");

    try {
      Student deletedStudent = studentService.deleteStudentById(input);
      boolean removedSchedule = scheduleService.removeScheduleByStudentId(input);
      System.out.println("Student with ID " + deletedStudent.getId() + " has been deleted.");
      if (removedSchedule) {
        System.out.println("Schedule for student ID " + input + " has also been removed.");
      }
    } catch (IllegalArgumentException | IllegalStateException e) {
      ConsoleIO.printError(e);
    }
  }

  private static boolean flushPendingStudentChanges() {
    try {
      studentService.flushPendingChanges();
      scheduleService.flushPendingChanges();
      return true;
    } catch (IllegalStateException e) {
      ConsoleIO.printError(e);
      ConsoleIO.pause();
      return false;
    }
  }
}
