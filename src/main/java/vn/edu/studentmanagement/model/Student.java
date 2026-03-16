package vn.edu.studentmanagement.model;

public class Student {
  private int id;
  private String fullname;
  private String major;
  private String gender;
  public Student() {
  }

  public Student(int id, String fullname, String major, String gender) {
    this.id = id;
    this.fullname = fullname;
    this.major = major;
    this.gender = gender;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getFullname() {
    return fullname;
  }

  public void setFullname(String fullname) {
    this.fullname = fullname;
  }

  public String getMajor() {
    return major;
  }

  public void setMajor(String major) {
    this.major = major;
  }
  public String getGender() {
    return gender;
  }
  public void setGender(String gender) {
    this.gender = gender; 
  }
  public String getLastname(){
    if(fullname != null && fullname.contains(" ")){
      String[] parts = fullname.split(" ");
      return parts[parts.length - 1];
    }
    return null;
  }

}
