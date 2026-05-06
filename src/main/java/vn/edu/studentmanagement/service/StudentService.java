package vn.edu.studentmanagement.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import vn.edu.studentmanagement.model.Gender;
import vn.edu.studentmanagement.model.Major;
import vn.edu.studentmanagement.model.Student;
import vn.edu.studentmanagement.storage.CsvRepository;
import vn.edu.studentmanagement.storage.CsvStudentRepository;

public class StudentService {
  private final CsvRepository<Student> studentRepository;

  public StudentService() {
    this(new CsvStudentRepository());
  }

  public StudentService(CsvRepository<Student> studentRepository) {
    this.studentRepository = studentRepository;
  }

  private List<Student> getStudents() {
    return studentRepository.readAll();
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
  private void validateStudentData(String name, String major, String gender) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Student name is required.");
    }
    if (major == null || major.trim().isEmpty()) {
      throw new IllegalArgumentException("Major is required.");
    }
    if (gender == null || gender.trim().isEmpty()) {
      throw new IllegalArgumentException("Gender is required.");
    }

    // Example of specific business logic:
    // Only allow specific gender strings
    String g = gender.trim().toLowerCase();
    if (!g.equals("male") && !g.equals("female") && !g.equals("other")) {
      throw new IllegalArgumentException("Gender must be 'Male', 'Female', or 'Other'.");
    }

    String m = major.trim().toUpperCase();
    if (!m.equals("IT") && !m.equals("CS") && !m.equals("DS")) {
      throw new IllegalArgumentException("Major must be 'IT', 'CS', or 'DS'.");
    }
  }

  public Student addStudent(String name, String major, String gender) {
    // 1. Validate the data first
    validateStudentData(name, major, gender);

    // 2. Sanitize inputs (remove commas to prevent CSV breakage)
    String cleanName = name.trim().replace(",", " ");
    String cleanMajor = major.trim().replace(",", " ");
    String cleanGender = gender.trim().replace(",", " ");

    List<Student> students = getStudents();
    int nextId = students.stream()
        .map(Student::getId)
        .mapToInt(id -> {
          try {
            return Integer.parseInt(id);
          } catch (NumberFormatException e) {
            return 0;
          }
        })
        .max()
        .orElse(0) + 1;

    Student s = new Student(
        String.valueOf(nextId),
        cleanName,
        Major.valueOf(cleanMajor.toUpperCase()),
        Gender.valueOf(cleanGender.toUpperCase()),
        0);
    studentRepository.append(s);

    return s;
  }

  // FIXED: Changed stt to id to match your Student model
  public Student deleteStudentById(int idToDelete) {
    List<Student> students = getStudents();
    if (students.isEmpty()) {
      throw new IllegalArgumentException("Empty list.");
    }

    Student deletedStudent = null;
    List<Student> newList = new ArrayList<>();

    for (Student s : students) {
      if (Integer.parseInt(s.getId()) == idToDelete) {
        deletedStudent = s;
        continue; // Skip adding to new list
      }
      newList.add(s);
    }

    if (deletedStudent != null) {
      studentRepository.writeAll(newList);
      return deletedStudent;
    } else {
      throw new IllegalArgumentException("ID not found: " + idToDelete);

    }
  }

  public Student findById(String id) {
    if (id == null || id.trim().isEmpty()) {
      throw new IllegalArgumentException("ID cannot be empty.");
    }
    return getStudents().stream()
        .filter(s -> s.getId().equals(id.trim()))
        .findFirst()
        .orElse(null);
  }
}
