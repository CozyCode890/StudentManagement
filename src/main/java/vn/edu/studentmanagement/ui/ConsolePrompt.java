package vn.edu.studentmanagement.ui;

public class ConsolePrompt {
  public static String trimmed(String message) {
    return ConsoleIO.prompt(message).trim();
  }

  public static String upperTrimmed(String message) {
    return trimmed(message).toUpperCase();
  }

  public static String courseIdOrBack(String message) {
    ConsoleIO.print(message);
    ConsoleIO.flush();

    StringBuilder input = new StringBuilder();
    boolean rawMode = TerminalController.enableRawMode();
    if (!rawMode) {
      return ConsoleIO.readLine().trim().toUpperCase();
    }

    try {
      while (true) {
        int ch = System.in.read();
        if (ch == -1 || ch == '\n' || ch == '\r') {
          ConsoleIO.println("");
          return input.toString().trim().toUpperCase();
        }

        if (input.isEmpty() && (ch == 'b' || ch == 'B')) {
          ConsoleIO.println(String.valueOf((char) ch));
          return "B";
        }

        if (ch == 127 || ch == 8) {
          if (!input.isEmpty()) {
            input.deleteCharAt(input.length() - 1);
            ConsoleIO.print("\b \b");
            ConsoleIO.flush();
          }
          continue;
        }

        input.append((char) ch);
        ConsoleIO.print(String.valueOf((char) ch));
        ConsoleIO.flush();
      }
    } catch (java.io.IOException e) {
      return input.toString().trim().toUpperCase();
    } finally {
      TerminalController.disableRawMode();
    }
  }
}
