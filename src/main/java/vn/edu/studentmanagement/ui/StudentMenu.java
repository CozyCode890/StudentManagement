package vn.edu.studentmanagement.ui;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

import vn.edu.studentmanagement.model.Student;
import vn.edu.studentmanagement.service.StudentService;
import vn.edu.studentmanagement.storage.CsvStudentRepository;

public class StudentMenu {
  private static final Scanner SC = new Scanner(System.in, StandardCharsets.UTF_8);

  public static void viewStudents() {
    List<Student> students = CsvStudentRepository.readAll();
    if (students.isEmpty()) {
      System.out.println("\n(No students yet)\n");
      return;
    }

    int sttWidth = Math.max(3, "STT".length());
    int nameWidth = Math.max(4, "Name".length());

    for (Student s : students) {
      sttWidth = Math.max(sttWidth, String.valueOf(s.getStt()).length());
      nameWidth = Math.max(nameWidth, s.getName().length());
    }

    String border = "+" + "-".repeat(sttWidth + 2) + "+" + "-".repeat(nameWidth + 2) + "+";
    String header = String.format("| %-" + sttWidth + "s | %-" + nameWidth + "s |", "STT", "Name");

    System.out.println();
    System.out.println(border);
    System.out.println(header);
    System.out.println(border);

    for (Student s : students) {
      System.out.printf("| %-" + sttWidth + "d | %-" + nameWidth + "s |%n", s.getStt(), s.getName());
    }

    System.out.println(border);
    System.out.println();
  }

  public static void addStudent() {
    System.out.print("\nEnter student name: ");
    String name = SC.nextLine();

    try {
      Student student = StudentService.addStudent(name);
      System.out.println("Added: STT=" + student.getStt() + ", Name=" + student.getName() + "\n");
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage() + "\n");
    } catch (Exception e) {
      System.out.println("Failed to add student: " + e.getMessage() + "\n");
    }
  }

  public static void deleteStudentByStt() {
    System.out.print("\nEnter STT to delete: ");
    String input = SC.nextLine().trim();

    try {
      Student student = StudentService.deleteStudentByStt(Integer.parseInt(input));
      System.out.println("Deleted: STT=" + student.getStt() + ", Name=" + student.getName() + "\n");
    } catch (NumberFormatException e) {
      System.out.println("Invalid STT.\n");
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage() + "\n");
    } catch (Exception e) {
      System.out.println("Failed to delete student: " + e.getMessage() + "\n");
    }
  }
}
