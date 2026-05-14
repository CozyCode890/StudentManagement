package vn.edu.studentmanagement.infrastructure.csv;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import vn.edu.studentmanagement.application.store.Repository;
import vn.edu.studentmanagement.application.store.RepositoryException;

public abstract class CsvRepository<T> implements Repository<T> {
  private final Path csvPath;
  private final String itemLabel;

  protected CsvRepository(Path csvPath, String itemLabel) {
    this.csvPath = csvPath;
    this.itemLabel = itemLabel;
  }

  protected Path getCsvPath() {
    return csvPath;
  }

  protected void ensureFileExists() {
    try {
      Files.createDirectories(csvPath.getParent());
      if (Files.notExists(csvPath)) {
        Files.createFile(csvPath);
      }
    } catch (IOException e) {
      throw new RepositoryException("Failed to create/open " + itemLabel + " CSV file.", e);
    }
  }
}
