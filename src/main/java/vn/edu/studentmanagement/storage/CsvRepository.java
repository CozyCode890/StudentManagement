package vn.edu.studentmanagement.storage;

import java.util.List;

public interface CsvRepository<T> {
  void ensureFileExists();

  List<T> readAll();

  void writeAll(List<T> items);

  void append(T item);
}
