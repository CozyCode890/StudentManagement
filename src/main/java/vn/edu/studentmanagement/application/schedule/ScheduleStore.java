package vn.edu.studentmanagement.application.schedule;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import vn.edu.studentmanagement.domain.model.Schedule;
import vn.edu.studentmanagement.domain.normalization.StudentNormalizer;
import vn.edu.studentmanagement.infrastructure.csv.CsvRepository;
import vn.edu.studentmanagement.infrastructure.csv.StorageException;

public class ScheduleStore {
  private static final int SAVE_BATCH_SIZE = 5;

  private final CsvRepository<Schedule> scheduleRepository;
  private final StudentNormalizer studentNormalizer;
  private int pendingScheduleChanges;

  public ScheduleStore(CsvRepository<Schedule> scheduleRepository, StudentNormalizer studentNormalizer) {
    this.scheduleRepository = Objects.requireNonNull(scheduleRepository);
    this.studentNormalizer = Objects.requireNonNull(studentNormalizer);
  }

  public Map<String, Schedule> loadSchedulesByStudentId() {
    try {
      Map<String, Schedule> schedulesByStudentId = new LinkedHashMap<>();
      for (Schedule schedule : scheduleRepository.readAll()) {
        if (schedule.getStudentId() != null && !schedule.getStudentId().isBlank()) {
          schedulesByStudentId.put(studentNormalizer.normalizeStudentId(schedule.getStudentId()), schedule);
        }
      }
      return schedulesByStudentId;
    } catch (StorageException e) {
      throw new IllegalStateException("Unable to load schedules: " + getCauseMessage(e), e);
    }
  }

  public void markChanged(Map<String, Schedule> schedulesByStudentId) {
    pendingScheduleChanges++;
    if (pendingScheduleChanges >= SAVE_BATCH_SIZE) {
      saveSchedules(schedulesByStudentId);
    }
  }

  public void flushPendingChanges(Map<String, Schedule> schedulesByStudentId) {
    if (pendingScheduleChanges > 0) {
      saveSchedules(schedulesByStudentId);
    }
  }

  private void saveSchedules(Map<String, Schedule> schedulesByStudentId) {
    try {
      scheduleRepository.writeAll(new ArrayList<>(schedulesByStudentId.values()));
      pendingScheduleChanges = 0;
    } catch (StorageException e) {
      throw new IllegalStateException("Unable to save schedules: " + getCauseMessage(e), e);
    }
  }

  private String getCauseMessage(StorageException e) {
    Throwable cause = e.getCause();
    return cause != null && cause.getMessage() != null ? cause.getMessage() : e.getMessage();
  }
}
