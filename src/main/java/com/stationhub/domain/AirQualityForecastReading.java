package com.stationhub.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.stationhub.rules.Rules;
import com.stationhub.time.Interval;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class AirQualityForecastReading extends Reading {

    private final Double pm25;
    private final Double alertLevel;

    @JsonCreator
    public AirQualityForecastReading(@JsonProperty("date") LocalDate date,
            @JsonProperty("interval") Interval interval,
            @JsonProperty("pm25") Double pm25,
            @JsonProperty("alertLevel") Double alertLevel) {
        super(date, interval);
        this.pm25 = pm25;
        this.alertLevel = alertLevel;
    }

    @Override
    public DataType type() {
        return DataType.AIR_QUALITY_FORECAST;
    }

    @Override
    public Optional<String> validate(Optional<Reading> previousValid) {
        return Rules.firstFailure(
                Rules.field("Pm25", pm25, Rules.atLeast(0)),
                Rules.field("AlertLevel", alertLevel, Rules.positive()));
    }

    @Override
    public Map<String, Object> values() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("pm25", pm25);
        map.put("alertLevel", alertLevel);
        return map;
    }
}
