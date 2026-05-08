package vn.edu.studentmanagement.ui;

public class TableFormatter {
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
