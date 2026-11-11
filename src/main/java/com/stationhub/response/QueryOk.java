package com.stationhub.response;

import com.stationhub.domain.Reading;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class QueryOk extends QueryResponse {

    private final List<DayResult> days;

    public QueryOk(LocalDateTime timestamp, LocalDateTime from, LocalDateTime to,
            List<Reading> found) {
        super(timestamp, Status.OK, from, to);
        this.days = group(found);
    }

    public List<DayResult> getDays() {
        return days;
    }

    private static List<DayResult> group(List<Reading> found) {
        Map<LocalDate, List<Reading>> byDay = new LinkedHashMap<>();

        for (Reading r : found) {
            byDay.computeIfAbsent(r.date(), d -> new ArrayList<>()).add(r);
        }

        List<DayResult> result = new ArrayList<>();

        for (Map.Entry<LocalDate, List<Reading>> day : byDay.entrySet()) {
            List<Map<String, Object>> rows = new ArrayList<>();

            for (Reading r : day.getValue()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("interval", r.interval());
                row.put("slot", r.interval().slot());
                row.putAll(r.values());
                rows.add(row);
            }
            result.add(new DayResult(day.getValue().get(0).type(), day.getKey(), rows));
        }

        return result;
    }
}
