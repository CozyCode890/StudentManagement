package vn.edu.studentmanagement.ui.console;

public class ConsolePause {
  public static void waitForEnter() {
    ConsoleIO.print("\nPress Enter to continue...");
    ConsoleIO.readLine();
  }
}
