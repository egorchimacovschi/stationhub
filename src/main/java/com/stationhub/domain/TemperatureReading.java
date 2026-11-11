package com.stationhub.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.stationhub.rules.Rules;
import com.stationhub.time.Interval;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class TemperatureReading extends Reading {

    private final Double celsius;

    @JsonCreator
    public TemperatureReading(@JsonProperty("date") LocalDate date,
            @JsonProperty("interval") Interval interval,
            @JsonProperty("celsius") Double celsius) {
        super(date, interval);
        this.celsius = celsius;
    }

    @Override
    public DataType type() {
        return DataType.TEMPERATURE;
    }

    @Override
    public Optional<String> validate(Optional<Reading> previousValid) {
        return Rules.field("Celsius", celsius, Rules.between(-60, 60));
    }

    @Override
    public Map<String, Object> values() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("celsius", celsius);
        return map;
    }
}
