package vn.edu.studentmanagement.domain.validation;

public class StudentValidator {
  private static final String NAME_PATTERN = "\\p{L}+(?:\\s+\\p{L}+)*";

  public void validateStudentData(String id, String name, String gender) {
    validateNewStudentId(id);
    validateStudentName(name);
    validateGender(gender);
  }

  public void validateNewStudentId(String id) {
    validateStudentIdFormat(id);
  }

  public void validateStudentIdFormat(String id) {
    validateRequiredStudentId(id);

    if (id.length() != 11) {
      throw new IllegalArgumentException("ID must be exactly 11 characters.");
    }

    if (!id.startsWith("IT")) {
      throw new IllegalArgumentException("ID must start with IT.");
    }

    String majorCode = id.substring(2, 4);
    if (!majorCode.equals("IT") && !majorCode.equals("DS") && !majorCode.equals("CS")) {
      throw new IllegalArgumentException("ID major code must be IT, DS, or CS.");
    }

    String programCode = id.substring(4, 6);
    if (!programCode.equals("IU") && !programCode.equals("WE")) {
      throw new IllegalArgumentException("ID program code must be IU or WE.");
    }

    int yearCode = parseNumber(id.substring(6, 8), "ID year code must be a number from 21 to 25.");
    if (yearCode < 21 || yearCode > 25) {
      throw new IllegalArgumentException("ID year code must be a number from 21 to 25.");
    }

    int sequenceNumber = parseNumber(id.substring(8, 11), "ID sequence number must be from 001 to 199.");
    if (sequenceNumber < 1 || sequenceNumber > 199) {
      throw new IllegalArgumentException("ID sequence number must be from 001 to 199.");
    }
  }

  public void validateRequiredStudentId(String id) {
    if (id == null || id.trim().isEmpty()) {
      throw new IllegalArgumentException("ID cannot be empty.");
    }
  }

  public void validateStudentName(String name) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Student name is required.");
    }

    if (!name.trim().matches(NAME_PATTERN)) {
      throw new IllegalArgumentException("Student name can only contain letters and spaces.");
    }
  }

  public void validateGender(String gender) {
    if (gender == null || gender.trim().isEmpty()) {
      throw new IllegalArgumentException("Gender is required.");
    }

    if (!gender.equals("MALE") && !gender.equals("FEMALE")) {
      throw new IllegalArgumentException("Gender must be 'Male', 'Female', 'M', or 'F'.");
    }
  }

  private int parseNumber(String value, String errorMessage) {
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(errorMessage);
    }
  }
}
