package vn.edu.studentmanagement.application.student;

import java.util.Map;
import java.util.Objects;

import vn.edu.studentmanagement.application.store.BufferedCsvStore;
import vn.edu.studentmanagement.domain.model.Student;
import vn.edu.studentmanagement.domain.normalization.StudentNormalizer;
import vn.edu.studentmanagement.infrastructure.csv.CsvRepository;

class StudentStore {
  private final BufferedCsvStore<Student> store;

  StudentStore(CsvRepository<Student> studentRepository, StudentNormalizer normalizer) {
    StudentNormalizer checkedNormalizer = Objects.requireNonNull(normalizer);
    this.store = new BufferedCsvStore<>(
        Objects.requireNonNull(studentRepository),
        student -> normalizeStudentId(student, checkedNormalizer),
        "students");
  }

  Map<String, Student> loadStudentsById() {
    return store.loadByKey();
  }

  void markChanged(Map<String, Student> studentsById) {
    store.markChanged(studentsById);
  }

  void flushPendingChanges(Map<String, Student> studentsById) {
    store.flushPendingChanges(studentsById);
  }

  private String normalizeStudentId(Student student, StudentNormalizer normalizer) {
    String id = student.getId();
    return id == null || id.isBlank() ? null : normalizer.normalizeStudentId(id);
  }
}
