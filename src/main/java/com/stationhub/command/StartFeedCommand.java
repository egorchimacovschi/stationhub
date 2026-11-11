package com.stationhub.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.stationhub.app.EngineContext;
import java.time.LocalDateTime;

public class StartFeedCommand extends AbstractCommand {

    @JsonCreator
    public StartFeedCommand(@JsonProperty("timestamp") LocalDateTime timestamp) {
        super("startFeed", timestamp);
    }

    @Override
    protected void doExecute(EngineContext context) {
        context.setFeedActive(true);
    }
}
