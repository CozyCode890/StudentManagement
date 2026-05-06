package vn.edu.studentmanagement.ui;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

import vn.edu.studentmanagement.model.Student;
import vn.edu.studentmanagement.service.StudentService;

public class StudentMenu {
  private static final Scanner SC = new Scanner(System.in, StandardCharsets.UTF_8);
  private static final StudentService studentService = new StudentService();

  public static void run() {
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
      if (choice.equals("2")) {
        System.out.print("Enter name keyword: ");
        String keyword = SC.nextLine().toLowerCase();
        students = students.stream()
            .filter(s -> s.getFullName().toLowerCase().contains(keyword))
            .toList();
      }

      viewStudentsPaginated(students);
    }
  }

  public static void viewStudentsPaginated(List<Student> allStudents) {
    int ROWS_PER_PAGE = 10;

    if (allStudents.isEmpty()) {
      System.out.println("\n(No students yet)\n");
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

  // 3. Update your renderTable to accept a starting STT
  private static void renderTable(List<Student> students, int startStt) {
    String format = "| %-4s | %-6s | %-20s | %-10s | %-15s |%n";
    String line = "+------+--------+----------------------+------------+-----------------+";

    System.out.println(line);
    System.out.printf(format, "STT", "ID", "Full Name", "Gender", "Major");
    System.out.println(line);

    int currentStt = startStt; // Start from the passed value instead of 1
    for (Student s : students) {
      System.out.printf(format,
          currentStt++,
          s.getId(),
          truncate(s.getFullName(), 20),
          truncate(String.valueOf(s.getGender()), 10),
          truncate(String.valueOf(s.getMajor()), 15));
    }
    System.out.println(line);
  }

  private static String truncate(String text, int length) {
    if (text == null)
      return "";
    return text.length() <= length ? text : text.substring(0, length - 3) + "...";
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
      System.out.println("Student with ID " + input + " has been deleted.");
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }
}
