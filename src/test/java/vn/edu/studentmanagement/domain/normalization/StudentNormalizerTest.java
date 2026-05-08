package vn.edu.studentmanagement.domain.normalization;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import vn.edu.studentmanagement.domain.model.Major;

class StudentNormalizerTest {
  private final StudentNormalizer normalizer = new StudentNormalizer();

  @Test
  void normalizeStudentIdTrimsAndUppercases() {
    assertEquals("ITITIU21001", normalizer.normalizeStudentId(" ititiu21001 "));
  }

  @Test
  void normalizeStudentNameTitleCasesAndCollapsesSpaces() {
    assertEquals("Nguyen Van A", normalizer.normalizeStudentName("  nguyen   van   a "));
  }

  @Test
  void normalizeGenderAcceptsFullAndShortInput() {
    assertEquals("MALE", normalizer.normalizeGender("m"));
    assertEquals("FEMALE", normalizer.normalizeGender("Female"));
  }

  @Test
  void extractMajorFromIdReadsMajorCode() {
    assertEquals(Major.DS, normalizer.extractMajorFromId("ITDSWE25003"));
  }
}
