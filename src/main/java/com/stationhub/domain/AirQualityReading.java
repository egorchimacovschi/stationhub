package com.stationhub.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.stationhub.rules.Rules;
import com.stationhub.time.Interval;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class AirQualityReading extends Reading {

    private static final double MAX_JUMP = 250;

    private final Double pm25;

    @JsonCreator
    public AirQualityReading(@JsonProperty("date") LocalDate date,
            @JsonProperty("interval") Interval interval,
            @JsonProperty("pm25") Double pm25) {
        super(date, interval);
        this.pm25 = pm25;
    }

    public Double pm25() {
        return pm25;
    }

    @Override
    public DataType type() {
        return DataType.AIR_QUALITY;
    }

    @Override
    public Optional<String> validate(Optional<Reading> previousValid) {
        Optional<String> fieldProblem = Rules.field("Pm25", pm25, Rules.atLeast(0));
        if (fieldProblem.isPresent()) {
            return fieldProblem;
        }
        return previousValid
                .map(AirQualityReading.class::cast)
                .filter(prev -> Math.abs(pm25 - prev.pm25()) > MAX_JUMP)
                .map(prev -> "Pm25 jumped by more than 250 compared with the previous reading;"
                        + " reading skipped.");
    }

    @Override
    public Map<String, Object> values() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("pm25", pm25);
        return map;
    }
}
