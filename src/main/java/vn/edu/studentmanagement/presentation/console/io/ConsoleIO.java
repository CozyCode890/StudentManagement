package vn.edu.studentmanagement.presentation.console.io;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ConsoleIO {
  private static final Scanner SC = new Scanner(System.in, StandardCharsets.UTF_8);

  public static String readLine() {
    return SC.nextLine();
  }

  public static void print(String message) {
    System.out.print(message);
  }

  public static void println(String message) {
    System.out.println(message);
  }

  public static void flush() {
    System.out.flush();
  }

  public static String prompt(String message) {
    print(message);
    return readLine();
  }
}
