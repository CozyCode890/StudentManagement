package vn.edu.studentmanagement.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import vn.edu.studentmanagement.model.Gender;
import vn.edu.studentmanagement.model.Major;
import vn.edu.studentmanagement.model.Student;
import vn.edu.studentmanagement.storage.CsvRepository;
import vn.edu.studentmanagement.storage.CsvStudentRepository;
import vn.edu.studentmanagement.storage.StorageException;

public class StudentService {
  private static final int SAVE_BATCH_SIZE = 5;

  private final CsvRepository<Student> studentRepository;
  private final Map<String, Student> studentsById = new LinkedHashMap<>();
  private int pendingStudentChanges;

  public StudentService() {
    this(new CsvStudentRepository());
  }

  public StudentService(CsvRepository<Student> studentRepository) {
    this.studentRepository = Objects.requireNonNull(studentRepository);
    loadStudents();
  }

  private List<Student> getStudents() {
    return new ArrayList<>(studentsById.values());
  }

  public List<Student> findAll() {
    return new ArrayList<>(getStudents());
  }

  public List<Student> findAllSortedById() {
    return getStudents().stream()
        .sorted(Comparator.comparing(Student::getId))
        .collect(Collectors.toList());
  }

  public List<Student> searchStudents(String query) {
    if (query == null || query.trim().isEmpty()) {
      return findAll();
    }

    String lowerQuery = query.toLowerCase().trim();

    return getStudents().stream()
        .filter(student ->
        // Matches ID
        String.valueOf(student.getId()).contains(lowerQuery) ||
        // Matches Full Name (Searching by Last Name)
            (student.getLastName() != null && student.getLastName().toLowerCase().contains(lowerQuery)) ||
            // Matches Gender
            (student.getGender() != null && student.getGender().toString().toLowerCase().equalsIgnoreCase(lowerQuery)) ||
            // Matches Major
            (student.getMajor() != null && student.getMajor().toString().toLowerCase().contains(lowerQuery)))
        .collect(Collectors.toList());
  }

  public List<Student> findAllSortedByLastName() {
    return getStudents().stream()
        .sorted(Comparator.comparing(Student::getLastName, Comparator.nullsLast(Comparator.naturalOrder())))
        .collect(Collectors.toList());
  }

  // FIXED: Corrected constructor arguments and method names (getId instead of
  // getStt)
  private void validateStudentData(String id, String name, String major, String gender) {
    if (id == null || id.trim().isEmpty()) {
      throw new IllegalArgumentException("ID is required.");
    }
    if (id.contains(",")) {
      throw new IllegalArgumentException("ID cannot contain commas.");
    }
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Student name is required.");
    }
    if (major == null || major.trim().isEmpty()) {
      throw new IllegalArgumentException("Major is required.");
    }
    if (gender == null || gender.trim().isEmpty()) {
      throw new IllegalArgumentException("Gender is required.");
    }

    String g = normalizeGender(gender);
    if (g == null) {
      throw new IllegalArgumentException("Gender must be 'Male', 'Female', 'M', or 'F'.");
    }

    String m = major.trim().toUpperCase();
    if (!m.equals("IT") && !m.equals("CS") && !m.equals("DS")) {
      throw new IllegalArgumentException("Major must be 'IT', 'CS', or 'DS'.");
    }
  }

  public Student addStudent(String id, String name, String major, String gender) {
    // 1. Validate the data first
    validateStudentData(id, name, major, gender);

    // 2. Sanitize inputs (remove commas to prevent CSV breakage)
    String cleanId = id.trim();
    String cleanName = name.trim().replace(",", " ");
    String cleanMajor = major.trim().replace(",", " ");
    String cleanGender = normalizeGender(gender);

    if (findById(cleanId) != null) {
      throw new IllegalArgumentException("ID already exists: " + cleanId);
    }

    Student s = new Student(
        cleanId,
        cleanName,
        Major.valueOf(cleanMajor.toUpperCase()),
        Gender.valueOf(cleanGender),
        0);
    studentsById.put(cleanId, s);
    markStudentChanged();

    return s;
  }

  // FIXED: Changed stt to id to match your Student model
  public Student deleteStudentById(String idToDelete) {
    if (idToDelete == null || idToDelete.trim().isEmpty()) {
      throw new IllegalArgumentException("ID cannot be empty.");
    }

    if (studentsById.isEmpty()) {
      throw new IllegalArgumentException("Empty list.");
    }

    String cleanId = idToDelete.trim();
    Student deletedStudent = studentsById.remove(cleanId);

    if (deletedStudent != null) {
      markStudentChanged();
      return deletedStudent;
    } else {
      throw new IllegalArgumentException("ID not found: " + cleanId);

    }
  }

  public Student findById(String id) {
    if (id == null || id.trim().isEmpty()) {
      throw new IllegalArgumentException("ID cannot be empty.");
    }
    return studentsById.get(id.trim());
  }

  public void flushPendingChanges() {
    if (pendingStudentChanges > 0) {
      saveStudents();
    }
  }

  private void loadStudents() {
    try {
      for (Student student : studentRepository.readAll()) {
        if (student.getId() != null && !student.getId().isBlank()) {
          studentsById.put(student.getId().trim(), student);
        }
      }
    } catch (StorageException e) {
      throw new IllegalStateException("Unable to load students: " + getCauseMessage(e), e);
    }
  }

  private void saveStudents() {
    try {
      studentRepository.writeAll(new ArrayList<>(studentsById.values()));
      pendingStudentChanges = 0;
    } catch (StorageException e) {
      throw new IllegalStateException("Unable to save students: " + getCauseMessage(e), e);
    }
  }

  private void markStudentChanged() {
    pendingStudentChanges++;
    if (pendingStudentChanges >= SAVE_BATCH_SIZE) {
      saveStudents();
    }
  }

  private String getCauseMessage(StorageException e) {
    Throwable cause = e.getCause();
    return cause != null && cause.getMessage() != null ? cause.getMessage() : e.getMessage();
  }

  private String normalizeGender(String gender) {
    String g = gender.trim().toLowerCase();
    return switch (g) {
      case "m", "male" -> "MALE";
      case "f", "female" -> "FEMALE";
      default -> null;
    };
  }
}
