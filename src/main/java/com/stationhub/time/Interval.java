
package com.stationhub.time;

import com.stationhub.exception.InvalidInputException;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public record Interval(int slot) {

    public static final int SLOTS_PER_DAY = 48;
    public static final int MINUTES_PER_SLOT = 30;

    public Interval {
        if (slot < 1 || slot > SLOTS_PER_DAY) {
            throw new InvalidInputException("Slot out of range: " + slot);
        }
    }

    @JsonCreator
    public static Interval parse(String text) {
        if (text == null || !text.matches("\\d{2}:\\d{2} - \\d{2}:\\d{2}")) {
            throw new InvalidInputException("Bad interval: " + text);
        }

        LocalTime start = LocalTime.parse(text.substring(0, 5));
        Interval interval = containing(start);

        if (!interval.toString().equals(text)) {
            throw new InvalidInputException("Bad Interval: " + text);
        }

        return interval;
    }

    public static Interval containing(LocalTime time) {
        return new Interval((time.getHour() * 60 + time.getMinute()) / MINUTES_PER_SLOT);
    }

    public LocalTime start() {
        return LocalTime.of(0, 0).plusMinutes((long) (slot - 1) * MINUTES_PER_SLOT);
    }

    public LocalTime end() {
        return start().plusMinutes(MINUTES_PER_SLOT);
    }

    @JsonValue
    @Override
    public String toString() {
        return start() + " - " + end();
    }
}
