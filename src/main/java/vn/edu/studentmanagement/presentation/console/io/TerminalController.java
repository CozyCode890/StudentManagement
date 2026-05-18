package vn.edu.studentmanagement.presentation.console.io;

public class TerminalController {
  public static void clearScreen() {
    System.out.print("\033[H\033[2J");
    System.out.flush();
  }

  static boolean enableRawMode() {
    return runNumbery("raw -echo");
  }

  static void disableRawMode() {
    runNumbery("sane");
  }

  private static boolean runNumbery(String args) {
    try {
      Process process = new ProcessBuilder("sh", "-c", "stty " + args + " < /dev/tty").start();
      return process.waitFor() == 0;
    } catch (java.io.IOException e) {
      return false;
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return false;
    }
  }
}
