package vn.edu.studentmanagement.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import vn.edu.studentmanagement.model.Course;
import vn.edu.studentmanagement.model.Schedule;

public class CsvScheduleRepository implements CsvRepository<Schedule> {
  public static final Path CSV_PATH = Paths.get(
      System.getProperty("user.home"),
      ".student-manager",
      "schedules.csv");

  private final CourseCatalog courseCatalog;

  public CsvScheduleRepository(CourseCatalog courseCatalog) {
    this.courseCatalog = Objects.requireNonNull(courseCatalog);
  }

  @Override
  public void ensureFileExists() {
    try {
      Files.createDirectories(CSV_PATH.getParent());
      if (Files.notExists(CSV_PATH)) {
        Files.createFile(CSV_PATH);
      }
    } catch (IOException e) {
      throw new StorageException("Failed to create/open schedule CSV file.", e);
    }
  }

  @Override
  public List<Schedule> readAll() {
    ensureFileExists();
    try {
      List<String> lines = Files.readAllLines(CSV_PATH, StandardCharsets.UTF_8);
      Map<String, Schedule> schedulesByStudentId = new LinkedHashMap<>();

      for (String line : lines) {
        if (line.trim().isEmpty()) {
          continue;
        }

        // CSV format: studentId,courseId
        String[] parts = line.split(",");
        if (parts.length < 2) {
          continue;
        }

        String studentId = parts[0].trim();
        String courseId = normalizeCourseId(parts[1]);
        if (studentId.isEmpty() || courseId.isEmpty()) {
          continue;
        }

        Course course = courseCatalog.createScheduledCourse(courseId);
        if (course == null) {
          continue;
        }

        Schedule schedule = schedulesByStudentId.computeIfAbsent(studentId, Schedule::new);
        schedule.getSelectedCourses().add(course);
      }

      return new ArrayList<>(schedulesByStudentId.values());
    } catch (IOException e) {
      throw new StorageException("Failed to read schedule CSV file.", e);
    } catch (IllegalStateException e) {
      throw new StorageException("Failed to build schedule from CSV file.", e);
    }
  }

  @Override
  public void writeAll(List<Schedule> schedules) {
    ensureFileExists();
    List<String> lines = schedules.stream()
        .flatMap(schedule -> schedule.getSelectedCourses().stream()
            .map(course -> schedule.getStudentId() + "," + course.getCourseId()))
        .collect(Collectors.toList());

    try {
      Files.write(CSV_PATH, lines, StandardCharsets.UTF_8,
          StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
    } catch (IOException e) {
      throw new StorageException("Failed to write schedule CSV file.", e);
    }
  }

  @Override
  public void append(Schedule schedule) {
    ensureFileExists();
    List<String> lines = schedule.getSelectedCourses().stream()
        .map(course -> schedule.getStudentId() + "," + course.getCourseId())
        .collect(Collectors.toList());

    if (lines.isEmpty()) {
      return;
    }

    try {
      Files.write(
          CSV_PATH,
          lines,
          StandardCharsets.UTF_8,
          StandardOpenOption.CREATE,
          StandardOpenOption.APPEND);
    } catch (IOException e) {
      throw new StorageException("Failed to append schedule CSV file.", e);
    }
  }

  private String normalizeCourseId(String courseId) {
    return courseId.trim().toUpperCase(Locale.ROOT);
  }
}
