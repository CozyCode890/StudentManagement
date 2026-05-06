package vn.edu.studentmanagement;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import vn.edu.studentmanagement.storage.CourseCatalog;
import vn.edu.studentmanagement.storage.CsvScheduleRepository;
import vn.edu.studentmanagement.storage.CsvStudentRepository;
import vn.edu.studentmanagement.ui.ConsoleIO;
import vn.edu.studentmanagement.ui.MainMenu;
import vn.edu.studentmanagement.ui.ScheduleMenu;
import vn.edu.studentmanagement.ui.StudentMenu;

public class App {
  private static final Scanner SC = new Scanner(System.in, StandardCharsets.UTF_8);

  public static void main(String[] args) {
    new CsvStudentRepository().ensureFileExists();
    new CsvScheduleRepository(new CourseCatalog()).ensureFileExists();

    while (true) {
      ConsoleIO.clearScreen();
      MainMenu.printMenu();
      String choice = SC.nextLine().trim();

      if (choice.equalsIgnoreCase("q") || choice.equals("0")) {
        System.out.println("Bye 👋");
        break;
      }

      switch (choice) {
        case "1" -> StudentMenu.run();
        case "2" -> ScheduleMenu.run();
        default -> {
          System.out.println("Invalid choice. Please select 1, 2, 0, or q.\n");
          System.out.print("Press Enter to continue...");
          SC.nextLine();
        }
      }
    }
  }
}
