package com.stationhub.command;

import com.stationhub.app.EngineContext;
import java.time.LocalDateTime;

public abstract class AbstractCommand implements Command {

    private final String name;
    private final LocalDateTime timestamp;

    protected AbstractCommand(String name, LocalDateTime timestamp) {
        this.name = name;
        this.timestamp = timestamp;
    }

    @Override
    public final LocalDateTime timestamp() {
        return timestamp;
    }

    @Override
    public final String name() {
        return name;
    }

    @Override
    public final void execute(EngineContext context) {
        context.clock().advanceTo(timestamp);
        doExecute(context);
    }

    protected abstract void doExecute(EngineContext context);
}
