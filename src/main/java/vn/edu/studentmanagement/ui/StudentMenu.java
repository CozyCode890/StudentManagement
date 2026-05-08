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
      TerminalController.clearScreen();

      System.out.println("\n=== STUDENT MANAGEMENT SYSTEM ===");
      System.out.println("1) View Student Menu (Search/List)");
      System.out.println("2) Add New Student");
      System.out.println("3) Delete Student (by ID)");
      System.out.println("0) Back to main menu");

      String choice = ConsolePrompt.trimmed("Choose: ");
      switch (choice) {
        case "1" -> viewStudentMenu();
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

  private static void viewStudentMenu() {
    while (true) {
      TerminalController.clearScreen();

      System.out.println("\n--- VIEW OPTIONS ---");
      System.out.println("1) Show All Students");
      System.out.println("2) Search by Name");
      System.out.println("0) Return");

      String choice = ConsolePrompt.trimmed("Choice: ");
      if (choice.equals("0"))
        break;

      List<Student> students = studentService.findAll();
      String emptyMessage = "Student list is empty.";
      if (choice.equals("1")) {
        viewStudentsPaginated(students, emptyMessage);
      } else if (choice.equals("2")) {
        String keyword = ConsoleIO.prompt("Enter name keyword: ");
        try {
          studentService.validateStudentName(keyword);
        } catch (IllegalArgumentException e) {
          ConsoleMessagePrinter.error(e);
          ConsolePause.waitForEnter();
          continue;
        }

        String lowerKeyword = keyword.toLowerCase();
        students = students.stream()
            .filter(s -> s.getFullName().toLowerCase().contains(lowerKeyword))
            .toList();
        emptyMessage = "No students matched your search.";
        viewStudentsPaginated(students, emptyMessage);
      } else {
        TerminalController.clearScreen();
        ConsoleMessagePrinter.warning("Invalid choice.");
        ConsolePause.waitForEnter();
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
      ConsoleMessagePrinter.warning(emptyMessage);
      ConsolePause.waitForEnter();
      return;
    }

    int totalStudents = allStudents.size();
    int totalPages = (int) Math.ceil((double) totalStudents / ROWS_PER_PAGE);
    int currentPage = 0;
    String feedback = null;

    while (true) {
      TerminalController.clearScreen();
      int start = currentPage * ROWS_PER_PAGE;
      int end = Math.min(start + ROWS_PER_PAGE, totalStudents);

      List<Student> pageSlice = allStudents.subList(start, end);

      if (feedback != null) {
        ConsoleMessagePrinter.warning(feedback);
        feedback = null;
      }

      System.out.println("\n--- Viewing Page " + (currentPage + 1) + " of " + totalPages + " ---");

      // Pass "start + 1" so STT shows correctly (e.g., 11, 12, 13... on page 2)
      renderTable(pageSlice, start + 1);

      System.out.println("\n[N] Next | [P] Previous | [B] Back");
      String choice = ConsolePrompt.upperTrimmed("Action: ");

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
    String line = TableFormatter.buildSeparator(sttWidth, idWidth, nameWidth, genderWidth, majorWidth);

    System.out.println(line);
    System.out.printf(format, "STT", "ID", "Full Name", "Gender", "Major");
    System.out.println(line);

    int currentStt = startStt;
    for (Student s : students) {
      System.out.printf(format,
          currentStt++,
          TableFormatter.safeText(s.getId()),
          TableFormatter.safeText(s.getFullName()),
          TableFormatter.safeText(String.valueOf(s.getGender())),
          TableFormatter.safeText(String.valueOf(s.getMajor())));
    }
    System.out.println(line);
  }

  private static int maxLength(List<Student> students, java.util.function.Function<Student, String> valueExtractor) {
    return students.stream()
        .map(valueExtractor)
        .map(TableFormatter::safeText)
        .mapToInt(String::length)
        .max()
        .orElse(0);
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
