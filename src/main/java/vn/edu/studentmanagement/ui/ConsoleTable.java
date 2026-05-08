package vn.edu.studentmanagement.ui;

import java.util.ArrayList;
import java.util.List;

public class ConsoleTable {
  private ConsoleTable() {
  }

  public static void print(List<String> headers, List<List<String>> rows) {
    int columnCount = getColumnCount(headers, rows);
    if (columnCount == 0) {
      return;
    }

    List<Integer> widths = calculateWidths(headers, rows, columnCount);
    String separator = buildSeparator(widths);

    System.out.println(separator);
    printRow(headers, widths);
    System.out.println(separator);
    for (List<String> row : rows) {
      printRow(row, widths);
    }
    System.out.println(separator);
  }

  private static int getColumnCount(List<String> headers, List<List<String>> rows) {
    int columnCount = headers.size();
    for (List<String> row : rows) {
      columnCount = Math.max(columnCount, row.size());
    }
    return columnCount;
  }

  private static List<Integer> calculateWidths(List<String> headers, List<List<String>> rows, int columnCount) {
    List<Integer> widths = new ArrayList<>();
    for (int column = 0; column < columnCount; column++) {
      int width = valueAt(headers, column).length();
      for (List<String> row : rows) {
        width = Math.max(width, valueAt(row, column).length());
      }
      widths.add(width);
    }
    return widths;
  }

  private static String buildSeparator(List<Integer> widths) {
    int[] widthValues = widths.stream().mapToInt(Integer::intValue).toArray();
    return TableFormatter.buildSeparator(widthValues);
  }

  private static void printRow(List<String> values, List<Integer> widths) {
    StringBuilder row = new StringBuilder("|");
    for (int column = 0; column < widths.size(); column++) {
      row.append(" ")
          .append(padRight(valueAt(values, column), widths.get(column)))
          .append(" |");
    }
    System.out.println(row);
  }

  private static String valueAt(List<String> values, int index) {
    if (index >= values.size()) {
      return "";
    }
    return TableFormatter.safeText(values.get(index));
  }

  private static String padRight(String value, int width) {
    return value + " ".repeat(width - value.length());
  }
}
