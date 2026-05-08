package vn.edu.studentmanagement.ui;

public class ConsoleMessagePrinter {
  public static void error(RuntimeException e) {
    error(e.getMessage());
  }

  public static void error(String message) {
    ConsoleIO.println("[ERROR] " + message);
  }

  public static void warning(String message) {
    ConsoleIO.println("[!] " + message);
  }

  public static void success(String message) {
    ConsoleIO.println("[OK] " + message);
  }
}
