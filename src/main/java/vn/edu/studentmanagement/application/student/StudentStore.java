package vn.edu.studentmanagement.application.student;

import java.util.Map;
import java.util.Objects;

import vn.edu.studentmanagement.application.store.BufferedRepositoryStore;
import vn.edu.studentmanagement.application.store.Repository;
import vn.edu.studentmanagement.domain.model.Student;
import vn.edu.studentmanagement.domain.normalization.StudentNormalizer;

class StudentStore {
  private final BufferedRepositoryStore<Student> store;

  StudentStore(Repository<Student> studentRepository, StudentNormalizer normalizer) {
    StudentNormalizer checkedNormalizer = Objects.requireNonNull(normalizer);
    this.store = new BufferedRepositoryStore<>(
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
