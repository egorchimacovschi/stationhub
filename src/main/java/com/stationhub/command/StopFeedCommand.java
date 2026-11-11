package com.stationhub.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.stationhub.app.EngineContext;
import java.time.LocalDateTime;

public class StopFeedCommand extends AbstractCommand {

    @JsonCreator
    public StopFeedCommand(@JsonProperty("timestamp") LocalDateTime timestamp) {
        super("stopFeed", timestamp);
    }

    @Override
    protected void doExecute(EngineContext context) {
        context.setFeedActive(false);
    }
}
