package com.stationhub.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.stationhub.io.TimeFormats;
import java.time.LocalDateTime;

@JsonPropertyOrder({ "command", "timestamp", "status", "from", "to", "days", "reason" })
public abstract class QueryResponse extends Response {

    private final String from;
    private final String to;

    protected QueryResponse(LocalDateTime timestamp, Status status,
            LocalDateTime from, LocalDateTime to) {
        super("query", timestamp, status);
        this.from = TimeFormats.format(from);
        this.to = TimeFormats.format(to);
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }
}
