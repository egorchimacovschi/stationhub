package com.stationhub.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.stationhub.domain.DataType;
import com.stationhub.domain.Reading;
import com.stationhub.time.Interval;
import java.time.LocalDate;
import java.time.LocalDateTime;

@JsonPropertyOrder({ "command", "timestamp", "status", "stream", "date", "interval", "slot", "reason" })
public class RejectedReading extends Response {

    private final DataType stream;
    private final LocalDate date;
    private final Interval interval;
    private final String reason;

    public RejectedReading(String command, LocalDateTime timestamp, Reading reading, String reason) {
        super(command, timestamp, Status.REJECTED);
        this.stream = reading.type();
        this.date = reading.date();
        this.interval = reading.interval();
        this.reason = reason;
    }

    public DataType getStream() {
        return stream;
    }

    public LocalDate getDate() {
        return date;
    }

    public Interval getInterval() {
        return interval;
    }

    public int getSlot() {
        return interval.slot();
    }

    public String getReason() {
        return reason;
    }
}
