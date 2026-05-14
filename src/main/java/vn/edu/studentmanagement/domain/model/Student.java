package vn.edu.studentmanagement.domain.model;

import java.util.Objects;

public class Student {
  private final String id;
  private final String fullName;
  private final Major major;
  private final Gender gender;

  public Student(String id, String fullName, Major major, Gender gender) {
    this.id = id;
    this.fullName = fullName;
    this.major = major;
    this.gender = gender;
  }

  public String getId() {
    return id;
  }

  public String getFullName() {
    return fullName;
  }

  public Gender getGender() {
    return gender;
  }

  public Major getMajor() {
    return major;
  }

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
