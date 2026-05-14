package vn.edu.studentmanagement.presentation.console.menu;

public class StudentMenuView {
  public void printMenu() {
    System.out.println("\n=== STUDENT MANAGEMENT SYSTEM ===");
    System.out.println("1) View Student Menu (Search/List)");
    System.out.println("2) Add New Student");
    System.out.println("3) Delete Student (Search/Select)");
    System.out.println("0) Back to main menu");
  }

  public void printViewOptions() {
    System.out.println("\n--- VIEW OPTIONS ---");
    System.out.println("1) Show All Students");
    System.out.println("2) Search by Name");
    System.out.println("0) Return");
  }
}
