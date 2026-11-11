package com.stationhub.command;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.stationhub.app.EngineContext;
import java.time.LocalDateTime;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "command")
@JsonSubTypes({
    @JsonSubTypes.Type(value = StartFeedCommand.class, name = "startFeed"),
    @JsonSubTypes.Type(value = StopFeedCommand.class, name = "stopFeed"),
    @JsonSubTypes.Type(value = PublishCommand.class, name = "publish"),
    @JsonSubTypes.Type(value = CorrectCommand.class, name = "correct"),
    @JsonSubTypes.Type(value = QueryCommand.class, name = "query")
})
public interface Command {

    LocalDateTime timestamp();

    String name();

    void execute(EngineContext context);
}
