package vn.edu.studentmanagement.application.student;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import vn.edu.studentmanagement.domain.model.Gender;
import vn.edu.studentmanagement.domain.model.Major;
import vn.edu.studentmanagement.domain.model.Student;
import vn.edu.studentmanagement.domain.normalization.StudentNormalizer;
import vn.edu.studentmanagement.domain.validation.StudentValidator;
import vn.edu.studentmanagement.infrastructure.csv.CsvRepository;
import vn.edu.studentmanagement.infrastructure.csv.CsvStudentRepository;

public class StudentService {
  private final StudentValidator validator;
  private final StudentNormalizer normalizer;
  private final StudentStore studentStore;
  private final Map<String, Student> studentsById;

  public StudentService() {
    this(new CsvStudentRepository());
  }

  public StudentService(CsvRepository<Student> studentRepository) {
    this(new StudentNormalizer(), Objects.requireNonNull(studentRepository));
  }

  private StudentService(StudentNormalizer normalizer, CsvRepository<Student> studentRepository) {
    this(new StudentValidator(normalizer), normalizer, studentRepository);
  }

  public StudentService(
      StudentValidator validator,
      StudentNormalizer normalizer,
      CsvRepository<Student> studentRepository) {
    this.validator = Objects.requireNonNull(validator);
    this.normalizer = Objects.requireNonNull(normalizer);
    this.studentStore = new StudentStore(Objects.requireNonNull(studentRepository), normalizer);
    this.studentsById = studentStore.loadStudentsById();
  }

  private List<Student> getStudents() {
    return new ArrayList<>(studentsById.values());
  }

  public List<Student> displayAll() {
    return new ArrayList<>(getStudents());
  }

  public List<Student> findAll() {
    return displayAll();
  }

  public List<Student> findByName(String keyword) {
    validator.validateStudentName(keyword);
    String lowerKeyword = keyword.toLowerCase().trim();

    return getStudents().stream()
        .filter(student -> student.getFullName().toLowerCase().contains(lowerKeyword))
        .collect(Collectors.toList());
  }

  public void validateStudentName(String name) {
    validator.validateStudentName(name);
  }

  public Student addStudent(String id, String name, String gender) {
    validator.validateStudentData(id, name, gender);

    String cleanId = normalizeStudentId(id);
    String cleanName = normalizeStudentName(name);
    String cleanGender = normalizer.normalizeGender(gender);
    Major major = normalizer.extractMajorFromId(cleanId);

    if (filterById(cleanId) != null) {
      throw new IllegalArgumentException("ID already exists: " + cleanId);
    }

    Student s = new Student(
        cleanId,
        cleanName,
        major,
        Gender.valueOf(cleanGender),
        0);
    studentsById.put(cleanId, s);
    markStudentChanged();

    return s;
  }

  public Student deleteStudentById(String idToDelete) {
    validator.validateExistingStudentId(idToDelete);

    if (studentsById.isEmpty()) {
      throw new IllegalArgumentException("Empty list.");
    }

    String cleanId = normalizeStudentId(idToDelete);
    Student deletedStudent = studentsById.remove(cleanId);

    if (deletedStudent != null) {
      markStudentChanged();
      return deletedStudent;
    }
    throw new IllegalArgumentException("ID not found: " + cleanId);
  }

  public Student findById(String id) {
    validator.validateExistingStudentId(id);
    return studentsById.get(normalizeStudentId(id));
  }

  public Student filterById(String id) {
    return findById(id);
  }

  public String normalizeStudentId(String id) {
    return normalizer.normalizeStudentId(id);
  }

  public String normalizeStudentName(String name) {
    return normalizer.normalizeStudentName(name);
  }

  public void flushPendingChanges() {
    studentStore.flushPendingChanges(studentsById);
  }

  private void markStudentChanged() {
    studentStore.markChanged(studentsById);
  }
}
