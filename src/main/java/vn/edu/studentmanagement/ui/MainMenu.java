package vn.edu.studentmanagement.ui;

import java.nio.file.Path;

import vn.edu.studentmanagement.storage.CsvStudentRepository;

public class MainMenu {
  private static final Path CSV_PATH = CsvStudentRepository.CSV_PATH;

  public static void printMenu() {
    System.out.println("==================================");
    System.out.println(" Student Management System");
    System.out.println("==================================");
    System.out.println("1) Manage students");
    System.out.println("2) Manage schedules");
    System.out.println("0) Quit");
    System.out.print("Choose: ");
  }
}
