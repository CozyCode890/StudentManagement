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
}
