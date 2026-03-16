package vn.edu.studentmanagement.ui;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

import vn.edu.studentmanagement.model.Student;
import vn.edu.studentmanagement.service.StudentService;
import vn.edu.studentmanagement.storage.CsvStudentRepository;

public class StudentMenu {
  private static final Scanner SC = new Scanner(System.in, StandardCharsets.UTF_8);
  // Create an instance if the service methods are not static
  private static final StudentService studentService = new StudentService();

  public static void viewStudents() {
    List<Student> students = CsvStudentRepository.readAll();
    if (students.isEmpty()) {
      System.out.println("\n(No students yet)\n");
      return;
    }


    int idWidth = Math.max(3, "ID".length());
    int nameWidth = Math.max(4, "Name".length());

    for (Student s : students) {
      idWidth = Math.max(idWidth, String.valueOf(s.getId()).length());
      nameWidth = Math.max(nameWidth, s.getFullname().length());
    }

    String border = "+" + "-".repeat(idWidth + 2) + "+" + "-".repeat(nameWidth + 2) + "+";
    String header = String.format("| %-" + idWidth + "s | %-" + nameWidth + "s |", "ID", "Name");

    System.out.println();
    System.out.println(border);
    System.out.println(header);
    System.out.println(border);

    for (Student s : students) {
      // Fixed: Use getId() and getFullname()
      System.out.printf("| %-" + idWidth + "d | %-" + nameWidth + "s |%n", s.getId(), s.getFullname());
    }

    System.out.println(border);
    System.out.println();
  }

  public static void addStudent() {
    System.out.print("\nEnter student name: ");
    String name = SC.nextLine();

    // Added: Prompting for missing required fields
    System.out.print("Enter major: ");
    String major = SC.nextLine();

    System.out.print("Enter gender: ");
    String gender = SC.nextLine();

    try {
      // Fixed: Passing all required parameters
      Student student = studentService.addStudent(name, major, gender);
      System.out.println("Added: ID=" + student.getId() + ", Name=" + student.getFullname() + "\n");
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage() + "\n");
    } catch (Exception e) {
      System.out.println("Failed to add student: " + e.getMessage() + "\n");
    }
  }

  public static void deleteStudentByStt() {
    System.out.print("\nEnter ID to delete: ");
    String input = SC.nextLine().trim();

    try {
      // Fixed: Use deleteStudentById and correct getters
      Student student = studentService.deleteStudentById(Integer.parseInt(input));
      System.out.println("Deleted: ID=" + student.getId() + ", Name=" + student.getFullname() + "\n");
    } catch (NumberFormatException e) {
      System.out.println("Invalid ID format.\n");
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage() + "\n");
    }
  }
}