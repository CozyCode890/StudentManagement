package vn.edu.studentmanagement.ui.view;

import vn.edu.studentmanagement.model.Student;
import vn.edu.studentmanagement.ui.console.ConsoleIO;

public class StudentView {
  public void printAdded(Student student) {
    ConsoleIO.println("Successfully added ID: " + student.getId());
  }

  public void printDeleted(Student student) {
    ConsoleIO.println("Student with ID " + student.getId() + " has been deleted.");
  }

  public void printScheduleRemoved(String studentId) {
    ConsoleIO.println("Schedule for student ID " + studentId + " has also been removed.");
  }
}
