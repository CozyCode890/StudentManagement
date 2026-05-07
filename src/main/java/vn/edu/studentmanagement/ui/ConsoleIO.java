package vn.edu.studentmanagement.ui;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ConsoleIO {
  private static final Scanner SC = new Scanner(System.in, StandardCharsets.UTF_8);

  public static void clearScreen() {
    System.out.print("\033[H\033[2J");
    System.out.flush();
  }

  public static String readLine() {
    return SC.nextLine();
  }

  public static String prompt(String message) {
    System.out.print(message);
    return readLine();
  }

  public static String promptTrimmed(String message) {
    return prompt(message).trim();
  }

  public static String promptUpperTrimmed(String message) {
    return promptTrimmed(message).toUpperCase();
  }

  public static String promptCourseIdOrBack(String message) {
    System.out.print(message);
    System.out.flush();

    StringBuilder input = new StringBuilder();
    boolean rawMode = enableRawMode();
    if (!rawMode) {
      return readLine().trim().toUpperCase();
    }

    try {
      while (true) {
        int ch = System.in.read();
        if (ch == -1 || ch == '\n' || ch == '\r') {
          System.out.println();
          return input.toString().trim().toUpperCase();
        }

        if (input.isEmpty() && (ch == 'b' || ch == 'B')) {
          System.out.println((char) ch);
          return "B";
        }

        if (ch == 127 || ch == 8) {
          if (!input.isEmpty()) {
            input.deleteCharAt(input.length() - 1);
            System.out.print("\b \b");
            System.out.flush();
          }
          continue;
        }

        input.append((char) ch);
        System.out.print((char) ch);
        System.out.flush();
      }
    } catch (java.io.IOException e) {
      return input.toString().trim().toUpperCase();
    } finally {
      disableRawMode();
    }
  }

  public static void pause() {
    System.out.print("\nPress Enter to continue...");
    readLine();
  }

  public static void printError(RuntimeException e) {
    printError(e.getMessage());
  }

  public static void printError(String message) {
    System.out.println("[ERROR] " + message);
  }

  public static void printWarning(String message) {
    System.out.println("[!] " + message);
  }

  public static void printSuccess(String message) {
    System.out.println("[OK] " + message);
  }

  public static String buildSeparator(int... widths) {
    StringBuilder line = new StringBuilder("+");
    for (int width : widths) {
      line.append("-".repeat(width + 2)).append("+");
    }
    return line.toString();
  }

  public static String safeText(String text) {
    return text == null ? "" : text;
  }

  private static boolean enableRawMode() {
    return runStty("raw -echo");
  }

  private static void disableRawMode() {
    runStty("sane");
  }

  private static boolean runStty(String args) {
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
