package vn.edu.studentmanagement.infrastructure.csv;

import java.util.List;

public interface CsvRepository<T> {
  void ensureFileExists();

  List<T> readAll();

  void writeAll(List<T> items);

  void append(T item);
}
