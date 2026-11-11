package com.stationhub.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.stationhub.app.EngineContext;
import java.time.LocalDateTime;

public class PublishCommand extends WriteCommand {

    @JsonCreator
    public PublishCommand(@JsonProperty("timestamp") LocalDateTime timestamp,
                @JsonProperty("payload") ReadingsPayload payload) {
        super("publish", timestamp, payload);
    }

    @Override
    protected boolean isAllowed(EngineContext context) {
        return context.isFeedActive();
    }
}
