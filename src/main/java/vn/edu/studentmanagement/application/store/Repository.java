package vn.edu.studentmanagement.application.store;

import java.util.List;

public interface Repository<T> {
  List<T> readAll();

  void writeAll(List<T> items);
}
