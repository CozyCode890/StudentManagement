package vn.edu.studentmanagement.ui;

public class ConsoleIO {
  public static void clearScreen() {
    System.out.print("\033[H\033[2J");
    System.out.flush();
  }
}
