package com.stationhub.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.stationhub.rules.Rules;
import com.stationhub.time.Interval;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class PrecipitationReading extends Reading {

    private final Double rainMm;
    private final Double snowMm;

    @JsonCreator
    public PrecipitationReading(@JsonProperty("date") LocalDate date,
            @JsonProperty("interval") Interval interval,
            @JsonProperty("rainMm") Double rainMm,
            @JsonProperty("snowMm") Double snowMm) {
        super(date, interval);
        this.rainMm = rainMm;
        this.snowMm = snowMm;
    }

    @Override
    public DataType type() {
        return DataType.PRECIPITATION;
    }

    @Override
    public Optional<String> validate(Optional<Reading> previousValid) {
        return Rules.firstFailure(
                Rules.field("RainMm", rainMm, Rules.atLeast(0)),
                Rules.field("SnowMm", snowMm, Rules.atLeast(0)));
    }

    @Override
    public Map<String, Object> values() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("rainMm", rainMm);
        map.put("snowMm", snowMm);
        return map;
    }
}
