package vn.edu.studentmanagement.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import vn.edu.studentmanagement.model.Student;

public class StudentTableRenderer {
  private StudentTableRenderer() {
  }

  public static void render(List<Student> students, int startStt) {
    List<List<String>> rows = new ArrayList<>();
    int currentStt = startStt;
    for (Student student : students) {
      rows.add(Arrays.asList(
          String.valueOf(currentStt++),
          student.getId(),
          student.getFullName(),
          String.valueOf(student.getGender()),
          String.valueOf(student.getMajor())));
    }

    ConsoleTable.print(List.of("STT", "ID", "Full Name", "Gender", "Major"), rows);
  }
}
