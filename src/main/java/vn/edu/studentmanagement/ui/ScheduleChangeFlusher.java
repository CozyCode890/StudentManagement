package vn.edu.studentmanagement.ui;

import java.util.Objects;

import vn.edu.studentmanagement.service.ScheduleService;

class ScheduleChangeFlusher {
  private final ScheduleService scheduleService;

  ScheduleChangeFlusher(ScheduleService scheduleService) {
    this.scheduleService = Objects.requireNonNull(scheduleService);
  }

  boolean flush() {
    try {
      scheduleService.flushPendingChanges();
      return true;
    } catch (IllegalStateException e) {
      ConsoleMessagePrinter.error(e);
      ConsolePause.waitForEnter();
      return false;
    }
  }
}
