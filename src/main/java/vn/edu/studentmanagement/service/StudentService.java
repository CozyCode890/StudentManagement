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

  public List<Student> findAllSortedByLastName() {
    return getStudents().stream()
            .sorted(Comparator.comparing(Student::getLastname, Comparator.nullsLast(Comparator.naturalOrder())))
            .collect(Collectors.toList());
  }

  // FIXED: Corrected constructor arguments and method names (getId instead of getStt)
  public Student addStudent(String name, String major, String gender) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Name cannot be empty.");
    }

    name = name.trim().replace(",", " ");

    List<Student> students = getStudents();
    // Changed getStt() to getId()
    int nextId = students.isEmpty() ? 1 : students.get(students.size() - 1).getId() + 1;

    // FIXED: Now matches the 4-arg constructor in Student.java
    Student student = new Student(nextId, name, major, gender);
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