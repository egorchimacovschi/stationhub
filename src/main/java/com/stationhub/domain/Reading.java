package com.stationhub.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.stationhub.time.Interval;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "stream")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TemperatureReading.class, name = "TEMPERATURE"),
        @JsonSubTypes.Type(value = TemperatureForecastReading.class, name = "TEMPERATURE_FORECAST"),
        @JsonSubTypes.Type(value = AirQualityReading.class, name = "AIR_QUALITY"),
        @JsonSubTypes.Type(value = AirQualityForecastReading.class, name = "AIR_QUALITY_FORECAST"),
        @JsonSubTypes.Type(value = WindReading.class, name = "WIND"),
        @JsonSubTypes.Type(value = PrecipitationReading.class, name = "PRECIPITATION")
})
public abstract sealed class Reading
        permits TemperatureReading, TemperatureForecastReading, AirQualityReading,
        AirQualityForecastReading, WindReading, PrecipitationReading {

    private final LocalDate date;
    private final Interval interval;

    protected Reading(LocalDate date, Interval interval) {
        this.date = date;
        this.interval = interval;
    }

    public final LocalDate date() {
        return date;
    }

    public final Interval interval() {
        return interval;
    }

    public final LocalDateTime startTime() {
        return date.atTime(interval.start());
    }

    public final LocalDateTime endTime() {
        return startTime().plusMinutes(Interval.MINUTES_PER_SLOT);
    }

    public abstract DataType type();

    public abstract Optional<String> validate(Optional<Reading> previousValid);

    public abstract Map<String, Object> values();
}
