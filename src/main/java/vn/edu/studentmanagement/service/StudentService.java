package vn.edu.studentmanagement.service;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import vn.edu.studentmanagement.model.Student;
import vn.edu.studentmanagement.model.Gender;
import vn.edu.studentmanagement.model.Major;
import vn.edu.studentmanagement.storage.StudentRepository;

public class StudentService {
  private final StudentRepository studentRepository;

  public StudentService(StudentRepository studentRepository) {
    this.studentRepository = Objects.requireNonNull(studentRepository);
  }

  public Student findById(String id) {
    if (id == null || id.isBlank()) {
      throw new IllegalArgumentException("ID cannot be empty.");
    }
    return studentRepository.findById(id.trim());
  }

  public List<Student> getAll() {
    return studentRepository.getAll();
  }

  public void add(Student s) {
    if (s == null) {
      throw new IllegalArgumentException("Student cannot be null.");
    }
    validateStudent(s);
    if (studentRepository.findById(s.getId()) != null) {
      throw new IllegalArgumentException("ID already exists.");
    }
    studentRepository.add(s);
  }

  public boolean deleteById(String id) {
    if (id == null || id.isBlank()) {
      throw new IllegalArgumentException("ID cannot be empty.");
    }
    return studentRepository.deleteById(id.trim());
  }

  private void validateStudent(Student s) {
    if (s.getId() == null || s.getId().trim().isEmpty()) {
      throw new IllegalArgumentException("ID cannot be empty.");
    }

    String id = s.getId().trim();
    if (!id.matches("^[A-Za-z0-9]{1,20}$")) {
      throw new IllegalArgumentException("Invalid ID format.");
    }
    if (s.getFullName() == null || s.getFullName().trim().isEmpty()) {
      throw new IllegalArgumentException("Full name cannot be empty.");
    }
    if (s.getGender() == null) {
      throw new IllegalArgumentException("Gender is required.");
    }
    if (s.getMajor() == null) {
      throw new IllegalArgumentException("Major is required.");
    }
    if (s.getAge() <= 0) {
      throw new IllegalArgumentException("Age must be a positive number.");
    }
  }

  public List<Student> sortById(List<Student> input) {
    if (input == null) {
      throw new IllegalArgumentException("Input list cannot be null.");
    }
    return input.stream().sorted((a, b) -> a.getId().compareTo(b.getId())).collect(Collectors.toList());
  }

  public List<Student> sortByAge(List<Student> input) {
    if (input == null) {
      throw new IllegalArgumentException("Input list cannot be null.");
    }
    return input.stream().sorted((a, b) -> Integer.compare(a.getAge(), b.getAge())).collect(Collectors.toList());
  }

  public List<Student> sortByLastNameAtoZ(List<Student> input) {
    if (input == null) {
      throw new IllegalArgumentException("Input list cannot be null.");
    }
    return input.stream()
        .sorted((a, b) -> {
          String lastA = a.getLastName();
          String lastB = b.getLastName();
          if (lastA == null && lastB == null) return 0;
          if (lastA == null) return 1;
          if (lastB == null) return -1;
          return lastA.compareToIgnoreCase(lastB);
        })
        .collect(Collectors.toList());
  }

  /**
   * Chain multiple filter conditions. Pass null to ignore a condition.
   */
  public List<Student> filterStudents(
      List<Student> input,
      String namePrefix,
      Gender gender,
      Major major,
      Integer minAge,
      Integer maxAge) {

    if (input == null) {
      throw new IllegalArgumentException("Input list cannot be null.");
    }

    String prefix = namePrefix == null ? null : namePrefix.trim().toLowerCase(Locale.ROOT);
    Integer min = minAge;
    Integer max = maxAge;

    return input.stream().filter(s -> {
      if (prefix != null) {
        String fullName = s.getFullName();
        if (fullName == null || !fullName.toLowerCase(Locale.ROOT).startsWith(prefix)) {
          return false;
        }
      }
      if (gender != null && s.getGender() != gender) {
        return false;
      }
      if (major != null && s.getMajor() != major) {
        return false;
      }
      if (min != null && s.getAge() < min) {
        return false;
      }
      if (max != null && s.getAge() > max) {
        return false;
      }
      return true;
    }).collect(Collectors.toList());
  }
}
