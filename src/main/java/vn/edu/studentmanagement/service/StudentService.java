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
  private static final String NAME_PATTERN = "\\p{L}+(?:\\s+\\p{L}+)*";

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

  private void validateStudentData(String id, String name, String gender) {
    validateStudentId(id);
    validateStudentName(name);
    validateGender(gender);
  }

  public void validateStudentId(String id) {
    if (id == null || id.trim().isEmpty()) {
      throw new IllegalArgumentException("ID is required.");
    }

    String cleanId = normalizeStudentId(id);
    if (cleanId.length() != 11) {
      throw new IllegalArgumentException("ID must be exactly 11 characters.");
    }

    if (!cleanId.startsWith("IT")) {
      throw new IllegalArgumentException("ID must start with IT.");
    }

    String majorCode = cleanId.substring(2, 4);
    if (!majorCode.equals("IT") && !majorCode.equals("DS") && !majorCode.equals("CS")) {
      throw new IllegalArgumentException("ID major code must be IT, DS, or CS.");
    }

    String programCode = cleanId.substring(4, 6);
    if (!programCode.equals("IU") && !programCode.equals("WE")) {
      throw new IllegalArgumentException("ID program code must be IU or WE.");
    }

    int yearCode = parseNumber(cleanId.substring(6, 8), "ID year code must be a number from 21 to 25.");
    if (yearCode < 21 || yearCode > 25) {
      throw new IllegalArgumentException("ID year code must be a number from 21 to 25.");
    }

    int sequenceNumber = parseNumber(cleanId.substring(8, 11), "ID sequence number must be from 001 to 199.");
    if (sequenceNumber < 1 || sequenceNumber > 199) {
      throw new IllegalArgumentException("ID sequence number must be from 001 to 199.");
    }
  }

  public void validateStudentName(String name) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Student name is required.");
    }

    if (!name.trim().matches(NAME_PATTERN)) {
      throw new IllegalArgumentException("Student name can only contain letters and spaces.");
    }
  }

  private void validateGender(String gender) {
    if (gender == null || gender.trim().isEmpty()) {
      throw new IllegalArgumentException("Gender is required.");
    }

    String g = normalizeGender(gender);
    if (g == null) {
      throw new IllegalArgumentException("Gender must be 'Male', 'Female', 'M', or 'F'.");
    }
  }

  private int parseNumber(String value, String errorMessage) {
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(errorMessage);
    }
  }

  public Student addStudent(String id, String name, String gender) {
    // 1. Validate the data first
    validateStudentData(id, name, gender);

    String cleanId = normalizeStudentId(id);
    String cleanName = name.trim();
    String cleanGender = normalizeGender(gender);
    Major major = extractMajorFromId(cleanId);

    if (findById(cleanId) != null) {
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

  // FIXED: Changed stt to id to match your Student model
  public Student deleteStudentById(String idToDelete) {
    validateStudentId(idToDelete);

    if (studentsById.isEmpty()) {
      throw new IllegalArgumentException("Empty list.");
    }

    String cleanId = normalizeStudentId(idToDelete);
    Student deletedStudent = studentsById.remove(cleanId);

    if (deletedStudent != null) {
      markStudentChanged();
      return deletedStudent;
    } else {
      throw new IllegalArgumentException("ID not found: " + cleanId);

    }
  }

  public Student findById(String id) {
    validateStudentId(id);
    return studentsById.get(normalizeStudentId(id));
  }

  public String normalizeStudentId(String id) {
    return id.trim().toUpperCase();
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
          studentsById.put(normalizeStudentId(student.getId()), student);
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

  private Major extractMajorFromId(String id) {
    return Major.valueOf(id.substring(2, 4));
  }
}
