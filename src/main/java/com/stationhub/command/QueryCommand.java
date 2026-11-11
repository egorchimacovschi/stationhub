package com.stationhub.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.stationhub.app.EngineContext;
import com.stationhub.domain.ClientTier;
import com.stationhub.domain.DataType;
import com.stationhub.domain.Reading;
import com.stationhub.exception.AccessDeniedException;
import com.stationhub.exception.InvalidInputException;
import com.stationhub.response.QueryDenied;
import com.stationhub.response.QueryMissing;
import com.stationhub.response.QueryOk;
import com.stationhub.time.Interval;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class QueryCommand extends AbstractCommand {

    public record QueryPayload(ClientTier tier, DataType dataType, String target,
                               LocalDateTime from, LocalDateTime to) { }

    private final QueryPayload payload;

    @JsonCreator
    public QueryCommand(@JsonProperty("timestamp") LocalDateTime timestamp,
                        @JsonProperty("payload") QueryPayload payload) {
        super("query", timestamp);

        if (payload == null || payload.tier() == null || payload.dataType() == null) {
            throw new InvalidInputException("query needs tier and dataType");
        }
        
        this.payload = payload;
    }

    @Override
    protected void doExecute(EngineContext context) {
        DataType type = payload.dataType();
        LocalDateTime from;
        LocalDateTime to;
        List<Reading> found;

        if (payload.target() == null) {
            from = payload.from();
            to = payload.to();
            found = context.store().range(type, from, to);
        } else if ("LATEST".equals(payload.target())) {
            Optional<Reading> latest = context.store().latest(type);
            from = latest.map(Reading::startTime).orElse(timestamp());
            to = latest.map(Reading::endTime).orElse(timestamp());
            found = latest.map(List::of).orElse(List.of());
        } else {
            LocalDateTime target = LocalDateTime.parse(payload.target());
            Interval interval = Interval.containing(target.toLocalTime());
            from = target.toLocalDate().atTime(interval.start());
            to = from.plusMinutes(Interval.MINUTES_PER_SLOT);
            found = context.store().find(type, target.toLocalDate(), interval)
                .map(List::of).orElse(List.of());
        }

        try {
            type.requireReadableBy(payload.tier());
            if (found.isEmpty()) {
                context.emit(new QueryMissing(timestamp(), from, to));
            } else {
                context.emit(new QueryOk(timestamp(), from, to, found));
            }
        } catch (AccessDeniedException e) {
            context.emit(new QueryDenied(timestamp(), from, to, e.getMessage()));
        }
    }
}
