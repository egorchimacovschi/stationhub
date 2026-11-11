package com.stationhub.time;

import java.time.Duration;
import java.time.LocalDateTime;

public class SimulationClock {

  private LocalDateTime now;

  public long advanceTo(LocalDateTime target) {
    long ticks = now == null ? 0 : Math.max(0, Duration.between(now, target).toMinutes());
    now = target;
    return ticks;
  }

  public LocalDateTime now() {
    return now;
  }
}
