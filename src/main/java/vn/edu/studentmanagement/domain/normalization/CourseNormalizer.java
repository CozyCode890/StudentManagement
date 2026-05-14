package vn.edu.studentmanagement.domain.normalization;

import java.util.Locale;

public class CourseNormalizer {
  public String normalizeCourseId(String courseId) {
    return courseId.trim().toUpperCase(Locale.ROOT);
  }
}
