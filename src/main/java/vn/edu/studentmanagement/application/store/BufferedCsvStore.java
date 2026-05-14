package vn.edu.studentmanagement.application.store;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import vn.edu.studentmanagement.infrastructure.csv.CsvRepository;
import vn.edu.studentmanagement.infrastructure.csv.StorageException;

public class BufferedCsvStore<T> {
  private static final int SAVE_BATCH_SIZE = 5;

  private final CsvRepository<T> repository;
  private final Function<T, String> keyExtractor;
  private final String itemLabel;
  private int pendingChanges;

  public BufferedCsvStore(
      CsvRepository<T> repository,
      Function<T, String> keyExtractor,
      String itemLabel) {
    this.repository = Objects.requireNonNull(repository);
    this.keyExtractor = Objects.requireNonNull(keyExtractor);
    this.itemLabel = Objects.requireNonNull(itemLabel);
  }

  public Map<String, T> loadByKey() {
    try {
      Map<String, T> itemsByKey = new LinkedHashMap<>();
      for (T item : repository.readAll()) {
        String key = keyExtractor.apply(item);
        if (key != null && !key.isBlank()) {
          itemsByKey.put(key, item);
        }
      }
      return itemsByKey;
    } catch (StorageException e) {
      throw new IllegalStateException("Unable to load " + itemLabel + ": " + getCauseMessage(e), e);
    }
  }

  public void markChanged(Map<String, T> itemsByKey) {
    pendingChanges++;
    if (pendingChanges >= SAVE_BATCH_SIZE) {
      save(itemsByKey);
    }
  }

  public void flushPendingChanges(Map<String, T> itemsByKey) {
    if (pendingChanges > 0) {
      save(itemsByKey);
    }
  }

  private void save(Map<String, T> itemsByKey) {
    try {
      repository.writeAll(new ArrayList<>(itemsByKey.values()));
      pendingChanges = 0;
    } catch (StorageException e) {
      throw new IllegalStateException("Unable to save " + itemLabel + ": " + getCauseMessage(e), e);
    }
  }

  private String getCauseMessage(StorageException e) {
    Throwable cause = e.getCause();
    return cause != null && cause.getMessage() != null ? cause.getMessage() : e.getMessage();
  }
}
