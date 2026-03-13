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
      Files.createDirectories(CSV_PATH.getParent()); // create ~/.student-manager
      if (Files.notExists(CSV_PATH)) {
        Files.createFile(CSV_PATH);
      }
    } catch (IOException e) {
      System.err.println("Failed to create/open CSV file: " + e.getMessage());
      System.exit(1);
    }
  }

  public static List<Student> readAll() {
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

  public static void writeAll(List<Student> students) {
    List<String> lines = students.stream()
        .map(s -> s.getStt() + "," + s.getName())
        .collect(Collectors.toList());
    try {
      Files.write(CSV_PATH, lines, StandardCharsets.UTF_8,
          StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
    } catch (IOException e) {
      System.err.println("Failed to write CSV: " + e.getMessage());
    }
  }

  public static void append(Student student) {
    try {
      Files.writeString(
          CSV_PATH,
          student.getStt() + "," + student.getName() + System.lineSeparator(),
          StandardCharsets.UTF_8,
          StandardOpenOption.APPEND);
    } catch (IOException e) {
      throw new RuntimeException("Failed to append to CSV", e);
    }
  }
}
