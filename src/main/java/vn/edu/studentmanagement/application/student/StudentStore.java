package vn.edu.studentmanagement.application.student;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import vn.edu.studentmanagement.domain.model.Student;
import vn.edu.studentmanagement.domain.normalization.StudentNormalizer;
import vn.edu.studentmanagement.infrastructure.csv.CsvRepository;
import vn.edu.studentmanagement.infrastructure.csv.StorageException;

class StudentStore {
  private static final int SAVE_BATCH_SIZE = 5;

  private final CsvRepository<Student> studentRepository;
  private final StudentNormalizer normalizer;
  private int pendingStudentChanges;

  StudentStore(CsvRepository<Student> studentRepository, StudentNormalizer normalizer) {
    this.studentRepository = Objects.requireNonNull(studentRepository);
    this.normalizer = Objects.requireNonNull(normalizer);
  }

  Map<String, Student> loadStudentsById() {
    try {
      Map<String, Student> studentsById = new LinkedHashMap<>();
      for (Student student : studentRepository.readAll()) {
        if (student.getId() != null && !student.getId().isBlank()) {
          studentsById.put(normalizer.normalizeStudentId(student.getId()), student);
        }
      }
      return studentsById;
    } catch (StorageException e) {
      throw new IllegalStateException("Unable to load students: " + getCauseMessage(e), e);
    }
  }

  void markChanged(Map<String, Student> studentsById) {
    pendingStudentChanges++;
    if (pendingStudentChanges >= SAVE_BATCH_SIZE) {
      saveStudents(studentsById);
    }
  }

  void flushPendingChanges(Map<String, Student> studentsById) {
    if (pendingStudentChanges > 0) {
      saveStudents(studentsById);
    }
  }

  private void saveStudents(Map<String, Student> studentsById) {
    try {
      studentRepository.writeAll(new ArrayList<>(studentsById.values()));
      pendingStudentChanges = 0;
    } catch (StorageException e) {
      throw new IllegalStateException("Unable to save students: " + getCauseMessage(e), e);
    }
  }

  private String getCauseMessage(StorageException e) {
    Throwable cause = e.getCause();
    return cause != null && cause.getMessage() != null ? cause.getMessage() : e.getMessage();
  }
}
