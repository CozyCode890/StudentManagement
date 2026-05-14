package vn.edu.studentmanagement.domain.normalization;

import java.util.ArrayList;
import java.util.List;

import vn.edu.studentmanagement.domain.model.Major;

public class StudentNormalizer {
  public String normalizeStudentId(String id) {
    if (id == null) {
      return null;
    }
    return id.trim().toUpperCase();
  }

  public String normalizeStudentName(String name) {
    if (name == null) {
      return null;
    }
    String cleanedName = name.trim();
    if (cleanedName.isEmpty()) {
      return "";
    }

    String[] words = cleanedName.toLowerCase().split("\\s+");
    List<String> normalizedWords = new ArrayList<>();
    for (String word : words) {
      normalizedWords.add(word.substring(0, 1).toUpperCase() + word.substring(1));
    }
    return String.join(" ", normalizedWords);
  }

  public String normalizeGender(String gender) {
    if (gender == null) {
      return null;
    }
    String g = gender.trim().toLowerCase();
    return switch (g) {
      case "m", "male" -> "MALE";
      case "f", "female" -> "FEMALE";
      default -> g.toUpperCase();
    };
  }

  public Major extractMajorFromId(String id) {
    return Major.valueOf(id.substring(2, 4));
  }
}
