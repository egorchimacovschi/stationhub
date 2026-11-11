package com.stationhub.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.stationhub.app.EngineContext;
import java.time.LocalDateTime;

public class CorrectCommand extends WriteCommand {

    @JsonCreator
    public CorrectCommand(@JsonProperty("timestamp") LocalDateTime timestamp,
                @JsonProperty("payload") ReadingsPayload payload) {
        super("correct", timestamp, payload);
    }

    @Override
    protected boolean isAllowed(EngineContext context) {
        return true;
    }
}
