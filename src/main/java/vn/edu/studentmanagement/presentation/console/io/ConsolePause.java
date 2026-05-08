package vn.edu.studentmanagement.presentation.console.io;

public class ConsolePause {
  public static void waitForEnter() {
    ConsoleIO.print("\nPress Enter to continue...");
    ConsoleIO.readLine();
  }
}
