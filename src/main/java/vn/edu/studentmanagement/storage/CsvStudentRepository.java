package vn.edu.studentmanagement.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import vn.edu.studentmanagement.model.Student;

public class CsvStudentRepository {
  public static final Path CSV_PATH = Paths.get(
          System.getProperty("user.home"),
          ".student-manager",
          "students.csv");

  public static void ensureFileExists() {
    try {
      Files.createDirectories(CSV_PATH.getParent());
      if (Files.notExists(CSV_PATH)) {
        Files.createFile(CSV_PATH);
      }
    } catch (IOException e) {
      System.err.println("Failed to create/open CSV file: " + e.getMessage());
      System.exit(1);
    }
  }

  public static List<Student> readAll() {
    ensureFileExists();
    try {
      List<String> lines = Files.readAllLines(CSV_PATH, StandardCharsets.UTF_8);
      List<Student> students = new ArrayList<>();

      for (String line : lines) {
        if (line.trim().isEmpty()) continue;

        // CSV format: id,fullname,major,gender
        String[] parts = line.split(",");
        if (parts.length < 4) continue;

        try {
          int id = Integer.parseInt(parts[0].trim());
          String fullname = parts[1].trim();
          String major = parts[2].trim();
          String gender = parts[3].trim();
          students.add(new Student(id, fullname, major, gender));
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

  public static void writeAll(List<Student> students) {
    List<String> lines = students.stream()
            .map(s -> s.getId() + "," + s.getFullname() + "," + s.getMajor() + "," + s.getGender())
            .collect(Collectors.toList());
    try {
      Files.write(CSV_PATH, lines, StandardCharsets.UTF_8,
              StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
    } catch (IOException e) {
      System.err.println("Failed to write CSV: " + e.getMessage());
    }
  }

  public static void append(Student student) {
    String line = student.getId() + "," + student.getFullname() + "," + student.getMajor() + "," + student.getGender() + System.lineSeparator();
    try {
      Files.writeString(
              CSV_PATH,
              line,
              StandardCharsets.UTF_8,
              StandardOpenOption.CREATE,
              StandardOpenOption.APPEND);
    } catch (IOException e) {
      System.err.println("Failed to append to CSV: " + e.getMessage());
    }
  }
}
