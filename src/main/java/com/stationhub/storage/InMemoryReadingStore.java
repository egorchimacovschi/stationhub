package com.stationhub.storage;

import com.stationhub.domain.DailyLog;
import com.stationhub.domain.DataType;
import com.stationhub.domain.Reading;
import com.stationhub.time.Interval;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.TreeMap;

public class InMemoryReadingStore implements ReadingStore {

    private final Map<DataType, NavigableMap<LocalDate, DailyLog>> logs = new EnumMap<>(DataType.class);

    @Override
    public void save(Reading reading) {
        logs.computeIfAbsent(reading.type(), t -> new TreeMap<>())
                .computeIfAbsent(reading.date(), d -> new DailyLog(reading.type(), d))
                .put(reading);
    }

    @Override
    public Optional<Reading> find(DataType type, LocalDate date, Interval interval) {
        return Optional.ofNullable(logs.get(type))
                .map(days -> days.get(date))
                .flatMap(log -> log.get(interval));
    }

    @Override
    public List<Reading> range(DataType type, LocalDateTime from, LocalDateTime to) {

        List<Reading> result = new ArrayList<>();

        NavigableMap<LocalDate, DailyLog> days = logs.get(type);

        if (days == null) {
            return result;
        }

        for (DailyLog log : days.subMap(from.toLocalDate(), true, to.toLocalDate(), true).values()) {
            for (Reading r : log.readings()) {
                if (!r.startTime().isBefore(from) && !r.endTime().isAfter(to)) {
                    result.add(r);
                }
            }
        }

        return result;
    }

    @Override
    public Optional<Reading> latest(DataType type) {
        NavigableMap<LocalDate, DailyLog> days = logs.get(type);

        if (days == null || days.isEmpty()) {
            return Optional.empty();
        }

        List<Reading> last = days.lastEntry().getValue().readings();

        return Optional.of(last.get(last.size() - 1));
    }

    @Override
    public Optional<Reading> previousValid(Reading reading) {
        NavigableMap<LocalDate, DailyLog> days = logs.get(reading.type());

        if (days == null) {
            return Optional.empty();
        }

        DailyLog sameDay = days.get(reading.date());

        if (sameDay != null) {
            for (int slot = reading.interval().slot() - 1; slot >= 1; slot--) {
                Optional<Reading> found = sameDay.get(new Interval(slot));
                if (found.isPresent()) {
                    return found;
                }
            }
        }

        LocalDate day = reading.date();

        while ((day = days.lowerKey(day)) != null) {
            List<Reading> list = days.get(day).readings();
            if (!list.isEmpty()) {
                return Optional.of(list.get(list.size() - 1));
            }
        }
        return Optional.empty();
    }

}
