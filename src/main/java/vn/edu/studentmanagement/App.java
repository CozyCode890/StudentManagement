package vn.edu.studentmanagement;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class App {
  private static final Scanner SC = new Scanner(System.in, StandardCharsets.UTF_8);
  private static final Path CSV_PATH = Paths.get(
      System.getProperty("user.home"),
      ".student-manager",
      "students.csv");

  // Simple record-like class (works on Java 8+)
  private static class Student {
    int stt;
    String name;

    Student(int stt, String name) {
      this.stt = stt;
      this.name = name;
    }
  }

  public static void main(String[] args) {
    ensureFileExists();

    while (true) {
      printMenu();
      String choice = SC.nextLine().trim();

      if (choice.equalsIgnoreCase("q") || choice.equals("0")) {
        System.out.println("Bye 👋");
        break;
      }

      switch (choice) {
        case "1" -> viewStudents();
        case "2" -> addStudent();
        case "3" -> deleteStudentByStt();
        default -> System.out.println("Invalid choice. Please select 1, 2, 3, 0, or q.\n");
      }
    }
  }

  private static void printMenu() {
    System.out.println("==================================");
    System.out.println(" Student CSV Manager");
    System.out.println(" File: " + CSV_PATH.toAbsolutePath());
    System.out.println("==================================");
    System.out.println("1) View students");
    System.out.println("2) Add student");
    System.out.println("3) Delete student (by STT)");
    System.out.println("0) Exit");
    System.out.println("q) Quit");
    System.out.print("Choose: ");
  }

  private static void ensureFileExists() {
    try {
      Files.createDirectories(CSV_PATH.getParent()); // create ~/.student-manager
      if (Files.notExists(CSV_PATH)) {
        Files.createFile(CSV_PATH);
      }
    } catch (IOException e) {
      System.err.println("Failed to create/open CSV file: " + e.getMessage());
      System.exit(1);
    }
  }

  private static List<Student> readAll() {
    try {
      List<String> lines = Files.readAllLines(CSV_PATH, StandardCharsets.UTF_8);
      List<Student> students = new ArrayList<>();

      for (String line : lines) {
        line = line.trim();
        if (line.isEmpty())
          continue;

        // CSV is: stt,name (name may include commas in theory; we keep it simple)
        int comma = line.indexOf(',');
        if (comma <= 0)
          continue;

        String sttStr = line.substring(0, comma).trim();
        String name = line.substring(comma + 1).trim();

        try {
          int stt = Integer.parseInt(sttStr);
          students.add(new Student(stt, name));
        } catch (NumberFormatException ignored) {
          // skip malformed lines
        }
      }
      return students;
    } catch (IOException e) {
      System.err.println("Failed to read CSV: " + e.getMessage());
      return Collections.emptyList();
    }
  }

  private static void writeAll(List<Student> students) {
    List<String> lines = students.stream()
        .map(s -> s.stt + "," + s.name)
        .collect(Collectors.toList());
    try {
      Files.write(CSV_PATH, lines, StandardCharsets.UTF_8,
          StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
    } catch (IOException e) {
      System.err.println("Failed to write CSV: " + e.getMessage());
    }
  }

  private static void viewStudents() {
    List<Student> students = readAll();
    if (students.isEmpty()) {
      System.out.println("\n(No students yet)\n");
      return;
    }

    int sttWidth = Math.max(3, "STT".length());
    int nameWidth = Math.max(4, "Name".length());

    for (Student s : students) {
      sttWidth = Math.max(sttWidth, String.valueOf(s.stt).length());
      nameWidth = Math.max(nameWidth, s.name.length());
    }

    String border = "+" + "-".repeat(sttWidth + 2) + "+" + "-".repeat(nameWidth + 2) + "+";
    String header = String.format("| %-" + sttWidth + "s | %-" + nameWidth + "s |", "STT", "Name");

    System.out.println();
    System.out.println(border);
    System.out.println(header);
    System.out.println(border);

    for (Student s : students) {
      System.out.printf("| %-" + sttWidth + "d | %-" + nameWidth + "s |%n", s.stt, s.name);
    }

    System.out.println(border);
    System.out.println();
  }

  private static void addStudent() {
    System.out.print("\nEnter student name: ");
    String name = SC.nextLine().trim();

    if (name.isEmpty()) {
      System.out.println("Name cannot be empty.\n");
      return;
    }

    // Keep CSV super simple: avoid commas in name (replace them)
    name = name.replace(",", " ");

    List<Student> students = readAll();
    int nextStt = 1;
    if (!students.isEmpty()) {
      nextStt = students.get(students.size() - 1).stt + 1; // keep file order, no sorting
    }

    students.add(new Student(nextStt, name));

    // Append as new last line
    try {
      Files.writeString(CSV_PATH, nextStt + "," + name + System.lineSeparator(),
          StandardCharsets.UTF_8, StandardOpenOption.APPEND);
    } catch (IOException e) {
      System.err.println("Failed to append to CSV: " + e.getMessage());
      return;
    }

    System.out.println("Added: STT=" + nextStt + ", Name=" + name + "\n");
  }

  private static void deleteStudentByStt() {
    List<Student> students = readAll();
    if (students.isEmpty()) {
      System.out.println("\n(No students to delete)\n");
      return;
    }

    System.out.print("\nEnter STT to delete: ");
    String input = SC.nextLine().trim();

    int sttToDelete;
    try {
      sttToDelete = Integer.parseInt(input);
    } catch (NumberFormatException e) {
      System.out.println("Invalid STT.\n");
      return;
    }

    boolean removed = false;
    List<Student> newList = new ArrayList<>();
    for (Student s : students) {
      if (s.stt == sttToDelete && !removed) {
        removed = true;
        continue; // skip this one
      }
      newList.add(s);
    }

    if (!removed) {
      System.out.println("STT not found: " + sttToDelete + "\n");
      return;
    }

    writeAll(newList);
    System.out.println("Deleted STT: " + sttToDelete + "\n");
  }
}
