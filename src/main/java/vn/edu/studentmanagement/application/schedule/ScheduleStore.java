package vn.edu.studentmanagement.application.schedule;

import java.util.Map;
import java.util.Objects;

import vn.edu.studentmanagement.application.store.BufferedCsvStore;
import vn.edu.studentmanagement.domain.model.Schedule;
import vn.edu.studentmanagement.domain.normalization.StudentNormalizer;
import vn.edu.studentmanagement.infrastructure.csv.CsvRepository;

class ScheduleStore {
  private final BufferedCsvStore<Schedule> store;

  ScheduleStore(CsvRepository<Schedule> scheduleRepository, StudentNormalizer studentNormalizer) {
    StudentNormalizer checkedNormalizer = Objects.requireNonNull(studentNormalizer);
    this.store = new BufferedCsvStore<>(
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
