package vn.edu.studentmanagement.storage;

import java.nio.file.Path;
import java.nio.file.Paths;

public class CsvStudentRepository {
  public static final Path CSV_PATH = Paths.get(
      System.getProperty("user.home"),
      ".student-manager",
      "students.csv");
}
