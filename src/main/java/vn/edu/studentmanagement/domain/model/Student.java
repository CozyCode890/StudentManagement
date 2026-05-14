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
