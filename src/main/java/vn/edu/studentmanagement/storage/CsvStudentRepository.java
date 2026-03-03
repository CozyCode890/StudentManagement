package vn.edu.studentmanagement.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CsvStudentRepository {
  public static final Path CSV_PATH = Paths.get(
      System.getProperty("user.home"),
      ".student-manager",
      "students.csv");

  public CsvStudentRepository() {
    ensureFileExists();
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
}
