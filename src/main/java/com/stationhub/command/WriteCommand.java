package com.stationhub.command;

import com.stationhub.app.EngineContext;
import com.stationhub.domain.Reading;
import com.stationhub.response.RejectedReading;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public abstract class WriteCommand extends AbstractCommand {

    public record ReadingsPayload(List<Reading> readings) { }

    private final List<Reading> readings;

    protected WriteCommand(String name, LocalDateTime timestamp, ReadingsPayload payload) {
        super(name, timestamp);
        
        this.readings = payload == null || payload.readings() == null
            ? List.of() : List.copyOf(payload.readings());
    }

    protected abstract boolean isAllowed(EngineContext context);

    @Override
    protected final void doExecute(EngineContext context) {

        if (!isAllowed(context)) {
            return;
        }
        
        for (Reading reading : readings) {
            Optional<String> problem = reading.validate(context.store().previousValid(reading));
        
            if (problem.isPresent()) {
                context.emit(new RejectedReading(name(), timestamp(), reading, problem.get()));
            } else {
                context.store().save(reading);
            }
        }
    }
}
