package vn.edu.studentmanagement.presentation.console.io;

import java.util.List;

public class ConsolePaginator {
  private ConsolePaginator() {
  }

  public static <T> void show(
      List<T> allItems,
      int rowsPerPage,
      String emptyMessage,
      PageRenderer<T> pageRenderer) {
    if (allItems.isEmpty()) {
      System.out.println();
      ConsoleMessagePrinter.warning(emptyMessage);
      ConsolePause.waitForEnter();
      return;
    }

    int totalItems = allItems.size();
    int totalPages = (int) Math.ceil((double) totalItems / rowsPerPage);
    int currentPage = 0;
    String feedback = null;

    while (true) {
      TerminalController.clearScreen();
      int start = currentPage * rowsPerPage;
      int end = Math.min(start + rowsPerPage, totalItems);

      if (feedback != null) {
        ConsoleMessagePrinter.warning(feedback);
        feedback = null;
      }

      System.out.println("\n--- Viewing Page " + (currentPage + 1) + " of " + totalPages + " ---");
      pageRenderer.render(allItems.subList(start, end), start + 1);

      System.out.println("\n[N] Next | [P] Previous | [B|0] Back");
      String choice = ConsolePrompt.upperTrimmed("Action: ");

      if (choice.equals("N") && currentPage < totalPages - 1) {
        currentPage++;
      } else if (choice.equals("P") && currentPage > 0) {
        currentPage--;
      } else if (choice.equals("B") || choice.equals("0")) {
        break;
      } else {
        feedback = "Invalid choice or no more pages.";
      }
    }
  }

  @FunctionalInterface
  public interface PageRenderer<T> {
    void render(List<T> pageItems, int startNumber);
  }
}
