package vn.edu.studentmanagement.app;

import java.util.Objects;

import vn.edu.studentmanagement.service.ScheduleService;
import vn.edu.studentmanagement.ui.console.ConsoleMessagePrinter;
import vn.edu.studentmanagement.ui.console.ConsolePause;

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
