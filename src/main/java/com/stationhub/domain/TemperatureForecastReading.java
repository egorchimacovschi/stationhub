package com.stationhub.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.stationhub.rules.Rules;
import com.stationhub.time.Interval;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class TemperatureForecastReading extends Reading {

    private final Double celsius;
    private final Double confidence;

    @JsonCreator
    public TemperatureForecastReading(@JsonProperty("date") LocalDate date,
            @JsonProperty("interval") Interval interval,
            @JsonProperty("celsius") Double celsius,
            @JsonProperty("confidence") Double confidence) {
        super(date, interval);
        this.celsius = celsius;
        this.confidence = confidence;
    }

    @Override
    public DataType type() {
        return DataType.TEMPERATURE_FORECAST;
    }

    @Override
    public Optional<String> validate(Optional<Reading> previousValid) {
        return Rules.firstFailure(
                Rules.field("Celsius", celsius, Rules.between(-60, 60)),
                Rules.field("Confidence", confidence, Rules.between(0, 1)));
    }

    @Override
    public Map<String, Object> values() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("celsius", celsius);
        map.put("confidence", confidence);
        return map;
    }
}
