package vn.edu.studentmanagement.presentation.console.controller;

import java.util.Objects;

import vn.edu.studentmanagement.application.schedule.ScheduleService;
import vn.edu.studentmanagement.presentation.console.io.ConsoleMessagePrinter;
import vn.edu.studentmanagement.presentation.console.io.ConsolePause;

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
