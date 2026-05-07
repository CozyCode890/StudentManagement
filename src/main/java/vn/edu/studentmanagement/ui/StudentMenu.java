package vn.edu.studentmanagement.ui;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import vn.edu.studentmanagement.model.Student;
import vn.edu.studentmanagement.service.ScheduleService;
import vn.edu.studentmanagement.service.StudentService;

public class StudentMenu {
  private static final Scanner SC = new Scanner(System.in, StandardCharsets.UTF_8);
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
      System.out.print("Choose: ");

      String choice = SC.nextLine().trim();
      switch (choice) {
        case "1" -> viewStudentMenu();
        case "2" -> addStudent();
        case "3" -> deleteStudentById();
        case "0" -> {
          studentService.flushPendingChanges();
          return;
        }
        default -> System.out.println("Invalid choice.");
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
      System.out.print("Choice: ");

      String choice = SC.nextLine().trim();
      if (choice.equals("0"))
        break;

      List<Student> students = studentService.findAll();
      String emptyMessage = "Student list is empty.";
      if (choice.equals("2")) {
        System.out.print("Enter name keyword: ");
        String keyword = SC.nextLine().toLowerCase();
        students = students.stream()
            .filter(s -> s.getFullName().toLowerCase().contains(keyword))
            .toList();
        emptyMessage = "No students matched your search.";
      }

      viewStudentsPaginated(students, emptyMessage);
    }
  }

  public static void viewStudentsPaginated(List<Student> allStudents) {
    viewStudentsPaginated(allStudents, "Student list is empty.");
  }

  private static void viewStudentsPaginated(List<Student> allStudents, String emptyMessage) {
    int ROWS_PER_PAGE = 10;

    if (allStudents.isEmpty()) {
      System.out.println("\n[!] " + emptyMessage);
      pause();
      return;
    }

    int totalStudents = allStudents.size();
    int totalPages = (int) Math.ceil((double) totalStudents / ROWS_PER_PAGE);
    int currentPage = 0;

    while (true) {
      int start = currentPage * ROWS_PER_PAGE;
      int end = Math.min(start + ROWS_PER_PAGE, totalStudents);

      List<Student> pageSlice = allStudents.subList(start, end);

      System.out.println("\n--- Viewing Page " + (currentPage + 1) + " of " + totalPages + " ---");

      // Pass "start + 1" so STT shows correctly (e.g., 11, 12, 13... on page 2)
      renderTable(pageSlice, start + 1);

      System.out.println("\n[N] Next | [P] Previous | [B] Back");
      System.out.print("Action: ");
      String choice = SC.nextLine().trim().toUpperCase();

      if (choice.equals("N") && currentPage < totalPages - 1) {
        currentPage++;
      } else if (choice.equals("P") && currentPage > 0) {
        currentPage--;
      } else if (choice.equals("B")) {
        break;
      } else {
        System.out.println("[!] Invalid choice or no more pages.");
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
    String line = buildSeparator(sttWidth, idWidth, nameWidth, genderWidth, majorWidth);

    System.out.println(line);
    System.out.printf(format, "STT", "ID", "Full Name", "Gender", "Major");
    System.out.println(line);

    int currentStt = startStt;
    for (Student s : students) {
      System.out.printf(format,
          currentStt++,
          safeText(s.getId()),
          safeText(s.getFullName()),
          safeText(String.valueOf(s.getGender())),
          safeText(String.valueOf(s.getMajor())));
    }
    System.out.println(line);
  }

  private static int maxLength(List<Student> students, java.util.function.Function<Student, String> valueExtractor) {
    return students.stream()
        .map(valueExtractor)
        .map(StudentMenu::safeText)
        .mapToInt(String::length)
        .max()
        .orElse(0);
  }

  private static String buildSeparator(int... widths) {
    StringBuilder line = new StringBuilder("+");
    for (int width : widths) {
      line.append("-".repeat(width + 2)).append("+");
    }
    return line.toString();
  }

  private static String safeText(String text) {
    return text == null ? "" : text;
  }

  public static void addStudent() {
    System.out.print("\nEnter ID: ");
    String id = SC.nextLine();
    System.out.print("\nEnter name: ");
    String name = SC.nextLine();
    System.out.print("Enter major: ");
    String major = SC.nextLine();
    System.out.print("Enter gender (Male/Female): ");
    String gender = SC.nextLine();

    try {
      Student student = studentService.addStudent(id, name, major, gender);
      System.out.println("Successfully added ID: " + student.getId());
    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
    }
  }

  public static void deleteStudentById() {
    System.out.print("\nEnter ID to delete: ");
    String input = SC.nextLine().trim();

    try {
      studentService.deleteStudentById(input);
      boolean removedSchedule = scheduleService.removeScheduleByStudentId(input);
      System.out.println("Student with ID " + input + " has been deleted.");
      if (removedSchedule) {
        System.out.println("Schedule for student ID " + input + " has also been removed.");
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  private static void pause() {
    System.out.print("\nPress Enter to continue...");
    SC.nextLine();
  }
}
