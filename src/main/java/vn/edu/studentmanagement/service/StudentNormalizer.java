package vn.edu.studentmanagement.service;

import java.util.ArrayList;
import java.util.List;

import vn.edu.studentmanagement.model.Major;

public class StudentNormalizer {
  public String normalizeStudentId(String id) {
    return id.trim().toUpperCase();
  }

  public String normalizeStudentName(String name) {
    String[] words = name.trim().toLowerCase().split("\\s+");
    List<String> normalizedWords = new ArrayList<>();
    for (String word : words) {
      normalizedWords.add(word.substring(0, 1).toUpperCase() + word.substring(1));
    }
    return String.join(" ", normalizedWords);
  }

  public String normalizeGender(String gender) {
    String g = gender.trim().toLowerCase();
    return switch (g) {
      case "m", "male" -> "MALE";
      case "f", "female" -> "FEMALE";
      default -> null;
    };
  }

  public Major extractMajorFromId(String id) {
    return Major.valueOf(id.substring(2, 4));
  }
}
