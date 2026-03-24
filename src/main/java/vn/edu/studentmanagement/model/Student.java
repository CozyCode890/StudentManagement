package vn.edu.studentmanagement.model;

import java.util.Objects;

public class Student {
  private String id;
  private String fullName;
  private Major major;
  private Gender gender;
  private int age;

  public Student(String id, String fullName, Major major, Gender gender, int age) {
    this.id = id;
    this.fullName = fullName;
    this.major = major;
    this.gender = gender;
    this.age = age;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public Gender getGender() {
    return gender;
  }

  public void setGender(Gender gender) {
    this.gender = gender;
  }

  public Major getMajor() {
    return major;
  }

  public void setMajor(Major major) {
    this.major = major;
  }

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }

  /**
   * Last name is extracted from the last non-empty token of {@link #fullName}.
   */
  public String getLastName() {
    if (fullName == null) {
      return null;
    }
    String[] parts = fullName.trim().split("\\s+");
    if (parts.length == 0) {
      return null;
    }
    return parts[parts.length - 1];
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof Student other))
      return false;
    return Objects.equals(id, other.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
