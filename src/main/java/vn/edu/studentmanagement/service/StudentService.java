package vn.edu.studentmanagement.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import vn.edu.studentmanagement.model.Student;
import vn.edu.studentmanagement.storage.CsvStudentRepository;

public class StudentService {
  private List<Student> getStudents() {
    return CsvStudentRepository.readAll();
  }

  public List<Student> findAll() {
    return new ArrayList<>(getStudents());
  }

  public List<Student> findAllSortedById() {
    return getStudents().stream()
            .sorted(Comparator.comparingInt(Student::getId))
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
                            (student.getLastname() != null && student.getLastname().toLowerCase().contains(lowerQuery)) ||
                            // Matches Gender
                            (student.getGender() != null && student.getGender().toLowerCase().equalsIgnoreCase(lowerQuery)) ||
                            // Matches Major
                            (student.getMajor() != null && student.getMajor().toLowerCase().contains(lowerQuery))
            )
            .collect(Collectors.toList());
  }

  public List<Student> findAllSortedByLastName() {
    return getStudents().stream()
            .sorted(Comparator.comparing(Student::getLastname, Comparator.nullsLast(Comparator.naturalOrder())))
            .collect(Collectors.toList());
  }

  // FIXED: Corrected constructor arguments and method names (getId instead of getStt)
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
  }
  public Student addStudent(String name, String major, String gender) {
    // 1. Validate the data first
    validateStudentData(name, major, gender);

    // 2. Sanitize inputs (remove commas to prevent CSV breakage)
    String cleanName = name.trim().replace(",", " ");
    String cleanMajor = major.trim().replace(",", " ");
    String cleanGender = gender.trim().replace(",", " ");

    List<Student> students = getStudents();
    int nextId = students.isEmpty() ? 1 : students.get(students.size() - 1).getId() + 1;

    Student student = new Student(nextId, cleanName, cleanMajor, cleanGender);
    CsvStudentRepository.append(student);

    return student;
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
      if (s.getId() == idToDelete) {
        deletedStudent = s;
        continue; // Skip adding to new list
      }
      newList.add(s);
    }

    if (deletedStudent != null) {
      CsvStudentRepository.writeAll(newList);
      return deletedStudent;
    } else {
      throw new IllegalArgumentException("ID not found: " + idToDelete);

    }
  }
}