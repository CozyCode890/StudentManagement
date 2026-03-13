package vn.edu.studentmanagement.ui;

import java.nio.file.Path;

import vn.edu.studentmanagement.storage.CsvStudentRepository;

public class MainMenu {
  private static final Path CSV_PATH = CsvStudentRepository.CSV_PATH;

  public static void printMenu() {
    System.out.println("==================================");
    System.out.println(" Student CSV Manager");
    System.out.println(" File: " + CSV_PATH.toAbsolutePath());
    System.out.println("==================================");
    System.out.println("1) View students");
    System.out.println("2) Add student");
    System.out.println("3) Delete student (by STT)");
    System.out.println("0) Exit");
    System.out.println("q) Quit");
    System.out.print("Choose: ");
  }
}
