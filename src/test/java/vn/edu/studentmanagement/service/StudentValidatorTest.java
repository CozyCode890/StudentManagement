package vn.edu.studentmanagement.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class StudentValidatorTest {
  private final StudentValidator validator = new StudentValidator();

  @Test
  void validateNewStudentIdAcceptsValidId() {
    assertDoesNotThrow(() -> validator.validateNewStudentId("ITITIU21001"));
  }

  @Test
  void validateNewStudentIdRejectsInvalidMajorCode() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> validator.validateNewStudentId("ITAAIU21001"));

    assertEquals("ID major code must be IT, DS, or CS.", exception.getMessage());
  }

  @Test
  void validateNewStudentIdRejectsInvalidYearCode() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> validator.validateNewStudentId("ITITIU26001"));

    assertEquals("ID year code must be a number from 21 to 25.", exception.getMessage());
  }

  @Test
  void validateExistingStudentIdOnlyRequiresNonBlankValue() {
    assertDoesNotThrow(() -> validator.validateExistingStudentId("SV001"));

    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> validator.validateExistingStudentId(" "));

    assertEquals("ID cannot be empty.", exception.getMessage());
  }

  @Test
  void validateStudentNameRejectsSpecialCharacters() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> validator.validateStudentName("Nguyen Van A!"));

    assertEquals("Student name can only contain letters and spaces.", exception.getMessage());
  }

  @Test
  void validateGenderRejectsUnknownInput() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> validator.validateGender("Unknown"));

    assertEquals("Gender must be 'Male', 'Female', 'M', or 'F'.", exception.getMessage());
  }
}
