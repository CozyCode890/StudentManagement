package vn.edu.studentmanagement.model;

public class Student {
  private int stt;
  private String name;

  public Student() {
  }

  public Student(int stt, String name) {
    this.stt = stt;
    this.name = name;
  }

  public int getStt() {
    return stt;
  }

  public void setStt(int stt) {
    this.stt = stt;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
