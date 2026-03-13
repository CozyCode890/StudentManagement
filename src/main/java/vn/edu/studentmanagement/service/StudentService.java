package vn.edu.studentmanagement.service;

import java.util.ArrayList;
import java.util.List;

import vn.edu.studentmanagement.model.Student;
import vn.edu.studentmanagement.storage.CsvStudentRepository;

public class StudentService {
  public static Student addStudent(String name) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Name cannot be empty.");
    }

    name = name.trim().replace(",", " ");

    List<Student> students = CsvStudentRepository.readAll();
    int nextStt = students.isEmpty() ? 1 : students.get(students.size() - 1).getStt() + 1;

    Student student = new Student(nextStt, name);
    CsvStudentRepository.append(student);

    return student;
  }

  public static Student deleteStudentByStt(int sttToDelete) {
    List<Student> students = CsvStudentRepository.readAll();
    if (students.isEmpty()) {
      throw new IllegalArgumentException("Empty list.");
    }

    Student deletedStudent = null;
    boolean removed = false;
    List<Student> newList = new ArrayList<>();
    for (Student s : students) {
      if (s.getStt() == sttToDelete && !removed) {
        deletedStudent = s;
        removed = true;
        continue;
      }
      newList.add(s);
    }

    if (removed) {
      CsvStudentRepository.writeAll(newList);
      return deletedStudent;
    } else {
      throw new IllegalArgumentException("STT not found: " + sttToDelete);
    }
  }
}
