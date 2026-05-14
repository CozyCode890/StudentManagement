package vn.edu.studentmanagement.domain.validation;

import java.util.Objects;

import vn.edu.studentmanagement.domain.normalization.StudentNormalizer;

public class StudentValidator {
  private static final String NAME_PATTERN = "\\p{L}+(?:\\s+\\p{L}+)*";

  private final StudentNormalizer normalizer;

  public StudentValidator() {
    this(new StudentNormalizer());
  }

  public StudentValidator(StudentNormalizer normalizer) {
    this.normalizer = Objects.requireNonNull(normalizer);
  }

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

    String cleanId = normalizer.normalizeStudentId(id);
    if (cleanId.length() != 11) {
      throw new IllegalArgumentException("ID must be exactly 11 characters.");
    }

    if (!cleanId.startsWith("IT")) {
      throw new IllegalArgumentException("ID must start with IT.");
    }

    String majorCode = cleanId.substring(2, 4);
    if (!majorCode.equals("IT") && !majorCode.equals("DS") && !majorCode.equals("CS")) {
      throw new IllegalArgumentException("ID major code must be IT, DS, or CS.");
    }

    String programCode = cleanId.substring(4, 6);
    if (!programCode.equals("IU") && !programCode.equals("WE")) {
      throw new IllegalArgumentException("ID program code must be IU or WE.");
    }

    int yearCode = parseNumber(cleanId.substring(6, 8), "ID year code must be a number from 21 to 25.");
    if (yearCode < 21 || yearCode > 25) {
      throw new IllegalArgumentException("ID year code must be a number from 21 to 25.");
    }

    int sequenceNumber = parseNumber(cleanId.substring(8, 11), "ID sequence number must be from 001 to 199.");
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

    String g = normalizer.normalizeGender(gender);
    if (g == null) {
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
