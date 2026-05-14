package vn.edu.studentmanagement.application.schedule;

import java.util.Map;
import java.util.Objects;

import vn.edu.studentmanagement.application.store.BufferedRepositoryStore;
import vn.edu.studentmanagement.application.store.Repository;
import vn.edu.studentmanagement.domain.model.Schedule;
import vn.edu.studentmanagement.domain.normalization.StudentNormalizer;

class ScheduleStore {
  private final BufferedRepositoryStore<Schedule> store;

  ScheduleStore(Repository<Schedule> scheduleRepository, StudentNormalizer studentNormalizer) {
    StudentNormalizer checkedNormalizer = Objects.requireNonNull(studentNormalizer);
    this.store = new BufferedRepositoryStore<>(
        Objects.requireNonNull(scheduleRepository),
        schedule -> normalizeStudentId(schedule, checkedNormalizer),
        "schedules");
  }

  Map<String, Schedule> loadSchedulesByStudentId() {
    return store.loadByKey();
  }

  void markChanged(Map<String, Schedule> schedulesByStudentId) {
    store.markChanged(schedulesByStudentId);
  }

  void flushPendingChanges(Map<String, Schedule> schedulesByStudentId) {
    store.flushPendingChanges(schedulesByStudentId);
  }

  private String normalizeStudentId(Schedule schedule, StudentNormalizer studentNormalizer) {
    String studentId = schedule.getStudentId();
    return studentId == null || studentId.isBlank() ? null : studentNormalizer.normalizeStudentId(studentId);
  }
}
