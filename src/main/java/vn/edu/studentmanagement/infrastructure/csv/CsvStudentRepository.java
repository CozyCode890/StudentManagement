package vn.edu.studentmanagement.infrastructure.csv;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import vn.edu.studentmanagement.application.store.RepositoryException;
import vn.edu.studentmanagement.domain.model.Gender;
import vn.edu.studentmanagement.domain.model.Major;
import vn.edu.studentmanagement.domain.model.Student;

public class CsvStudentRepository extends CsvRepository<Student> {
  public static final Path CSV_PATH = Paths.get(
      System.getProperty("user.home"),
      ".student-manager",
      "students.csv");

  public CsvStudentRepository() {
    super(CSV_PATH, "student");
  }

  @Override
  public List<Student> readAll() {
    ensureFileExists();
    try {
      List<String> lines = Files.readAllLines(getCsvPath(), StandardCharsets.UTF_8);
      List<Student> students = new ArrayList<>();

      for (String line : lines) {
        if (line.trim().isEmpty())
          continue;

        String[] parts = line.split(",");
        if (parts.length < 4)
          continue;

        try {
          String id = parts[0].trim();
          String fullName = parts[1].trim();
          Major major = Major.valueOf(parts[2].trim().toUpperCase());
          Gender gender = Gender.valueOf(parts[3].trim().toUpperCase());
          students.add(new Student(id, fullName, major, gender));
        } catch (IllegalArgumentException ignored) {
        }
      }
      return students;
    } catch (IOException e) {
      throw new RepositoryException("Failed to read student CSV file.", e);
    }
  }

  @Override
  public void writeAll(List<Student> students) {
    ensureFileExists();
    List<String> lines = students.stream()
        .map(s -> s.getId() + "," + s.getFullName() + "," + s.getMajor() + "," + s.getGender())
        .collect(Collectors.toList());
    try {
      Files.write(getCsvPath(), lines, StandardCharsets.UTF_8,
          StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
    } catch (IOException e) {
      throw new RepositoryException("Failed to write student CSV file.", e);
    }
  }

}
