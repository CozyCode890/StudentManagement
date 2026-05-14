package vn.edu.studentmanagement.support;

import java.util.ArrayList;
import java.util.List;

import vn.edu.studentmanagement.application.store.Repository;

public class InMemoryRepository<T> implements Repository<T> {
  private List<T> items;
  private List<T> lastWrittenItems = List.of();

  public InMemoryRepository() {
    this(List.of());
  }

  public InMemoryRepository(List<T> items) {
    this.items = new ArrayList<>(items);
  }

  @Override
  public List<T> readAll() {
    return new ArrayList<>(items);
  }

  @Override
  public void writeAll(List<T> items) {
    this.items = new ArrayList<>(items);
    this.lastWrittenItems = new ArrayList<>(items);
  }

  public List<T> getLastWrittenItems() {
    return new ArrayList<>(lastWrittenItems);
  }
}
