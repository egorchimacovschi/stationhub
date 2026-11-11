package com.stationhub.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.stationhub.rules.Rules;
import com.stationhub.time.Interval;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class WindReading extends Reading {

    private final Double speedKmh;
    private final Double gustKmh;

    @JsonCreator
    public WindReading(@JsonProperty("date") LocalDate date,
            @JsonProperty("interval") Interval interval,
            @JsonProperty("speedKmh") Double speedKmh,
            @JsonProperty("gustKmh") Double gustKmh) {
        super(date, interval);
        this.speedKmh = speedKmh;
        this.gustKmh = gustKmh;
    }

    @Override
    public DataType type() {
        return DataType.WIND;
    }

    @Override
    public Optional<String> validate(Optional<Reading> previousValid) {
        return Rules.firstFailure(
                Rules.field("SpeedKmh", speedKmh, Rules.between(0, 400)),
                Rules.field("GustKmh", gustKmh, Rules.between(0, 400)));
    }

    @Override
    public Map<String, Object> values() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("speedKmh", speedKmh);
        map.put("gustKmh", gustKmh);
        return map;
    }
}
