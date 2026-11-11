package com.stationhub.domain;

import com.stationhub.time.Interval;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class DailyLog {

    private final DataType type;
    private final LocalDate date;
    private final Reading[] slots = new Reading[Interval.SLOTS_PER_DAY];

    public DailyLog(DataType type, LocalDate date) {
        this.date = date;
        this.type = type;
    }

    public DataType type() {
        return type;
    }

    public LocalDate date() {
        return date;
    }

    public void put(Reading reading) {
        slots[reading.interval().slot() - 1] = reading;
    }

    public Optional<Reading> get(Interval interval) {
        return Optional.ofNullable(slots[interval.slot() - 1]);
    }

    public List<Reading> readings() {
        List<Reading> result = new ArrayList<>();

        for (Reading r : slots) {
            if (r != null) {
                result.add(r);
            }
        }

        return Collections.unmodifiableList(result);
    }
}
