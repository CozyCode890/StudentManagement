package vn.edu.studentmanagement.application.student;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import vn.edu.studentmanagement.application.store.Repository;
import vn.edu.studentmanagement.domain.model.Gender;
import vn.edu.studentmanagement.domain.model.Major;
import vn.edu.studentmanagement.domain.model.Student;
import vn.edu.studentmanagement.domain.normalization.StudentNormalizer;
import vn.edu.studentmanagement.domain.validation.StudentValidator;

public class StudentService {
  private final StudentValidator validator;
  private final StudentNormalizer normalizer;
  private final StudentStore studentStore;
  private final Map<String, Student> studentsById;

  public StudentService(Repository<Student> studentRepository) {
    this(new StudentNormalizer(), Objects.requireNonNull(studentRepository));
  }

  private StudentService(StudentNormalizer normalizer, Repository<Student> studentRepository) {
    this(new StudentValidator(), normalizer, studentRepository);
  }

  public StudentService(
      StudentValidator validator,
      StudentNormalizer normalizer,
      Repository<Student> studentRepository) {
    this.validator = Objects.requireNonNull(validator);
    this.normalizer = Objects.requireNonNull(normalizer);
    this.studentStore = new StudentStore(Objects.requireNonNull(studentRepository), normalizer);
    this.studentsById = studentStore.loadStudentsById();
  }

  public List<Student> findAll() {
    return new ArrayList<>(studentsById.values());
  }

  public List<Student> findByName(String keyword) {
    validator.validateStudentName(keyword);
    String lowerKeyword = keyword.toLowerCase().trim();

    return studentsById.values().stream()
        .filter(student -> student.getFullName().toLowerCase().contains(lowerKeyword))
        .collect(Collectors.toList());
  }

  public void validateNewStudentId(String id) {
    String cleanId = normalizer.normalizeStudentId(id);
    validator.validateNewStudentId(cleanId);

    if (studentsById.containsKey(cleanId)) {
      throw new IllegalArgumentException("ID already exists: " + cleanId);
    }
  }

  public void validateStudentName(String name) {
    validator.validateStudentName(normalizer.normalizeStudentName(name));
  }

  public void validateGender(String gender) {
    validator.validateGender(normalizer.normalizeGender(gender));
  }

  public Student addStudent(String id, String name, String gender) {
    String cleanId = normalizer.normalizeStudentId(id);
    String cleanName = normalizer.normalizeStudentName(name);
    String cleanGender = normalizer.normalizeGender(gender);

    validateNormalizedNewStudentId(cleanId);
    validator.validateStudentName(cleanName);
    validator.validateGender(cleanGender);
    Major major = normalizer.extractMajorFromId(cleanId);

    Student s = new Student(
        cleanId,
        cleanName,
        major,
        Gender.valueOf(cleanGender));
    studentsById.put(cleanId, s);
    markStudentChanged();

    return s;
  }

  public Student deleteStudentById(String idToDelete) {
    String cleanId = normalizer.normalizeStudentId(idToDelete);
    validator.validateStudentIdFormat(cleanId);
    return removeStudentById(cleanId);
  }

  public Student deleteExistingStudent(Student existingStudent) {
    Objects.requireNonNull(existingStudent);
    String cleanId = existingStudent.getId();
    validator.validateStudentIdFormat(cleanId);
    return removeStudentById(cleanId);
  }

  private Student removeStudentById(String cleanId) {
    if (studentsById.isEmpty()) {
      throw new IllegalArgumentException("Empty list.");
    }

    Student deletedStudent = studentsById.remove(cleanId);

    if (deletedStudent != null) {
      markStudentChanged();
      return deletedStudent;
    }
    throw new IllegalArgumentException("ID not found: " + cleanId);
  }

  public Student findById(String id) {
    validator.validateRequiredStudentId(id);
    return studentsById.get(normalizer.normalizeStudentId(id));
  }

  public Student findRequiredById(String id) {
    Student student = findById(id);
    if (student == null) {
      throw new IllegalArgumentException("ID not found: " + normalizer.normalizeStudentId(id));
    }
    return student;
  }

  public void flushPendingChanges() {
    studentStore.flushPendingChanges(studentsById);
  }

  private void markStudentChanged() {
    studentStore.markChanged(studentsById);
  }

  private void validateNormalizedNewStudentId(String cleanId) {
    validator.validateNewStudentId(cleanId);

    if (studentsById.containsKey(cleanId)) {
      throw new IllegalArgumentException("ID already exists: " + cleanId);
    }
  }

}
