package com.stationhub.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.stationhub.io.TimeFormats;
import java.time.LocalDateTime;

@JsonPropertyOrder({ "command", "timestamp", "status" })
public abstract class Response {

    public enum Status {
        REJECTED, OK, MISSING, DENIED
    }

    private final String command;
    private final String timestamp;
    private final Status status;

    protected Response(String command, LocalDateTime timestamp, Status status) {
        this.command = command;
        this.timestamp = TimeFormats.format(timestamp);
        this.status = status;
    }

    public String getCommand() {
        return command;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public Status getStatus() {
        return status;
    }
}
