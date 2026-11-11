package com.stationhub.response;

import com.stationhub.domain.DataType;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/** The readings of one stream for one day, as shown in a successful query. */
public record DayResult(DataType stream, LocalDate date, List<Map<String, Object>> readings) {
}
