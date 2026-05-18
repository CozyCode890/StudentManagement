package vn.edu.studentmanagement.presentation.console.renderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import vn.edu.studentmanagement.domain.model.Student;

public class StudentTableRenderer {
  private StudentTableRenderer() {
  }

  public static void render(List<Student> students, int startNumber) {
    List<List<String>> rows = new ArrayList<>();
    int currentNumber = startNumber;
    for (Student student : students) {
      rows.add(Arrays.asList(
          String.valueOf(currentNumber++),
          student.getId(),
          student.getFullName(),
          String.valueOf(student.getGender()),
          String.valueOf(student.getMajor())));
    }

    ConsoleTable.print(List.of("STT", "ID", "Full Name", "Gender", "Major"), rows);
  }
}
