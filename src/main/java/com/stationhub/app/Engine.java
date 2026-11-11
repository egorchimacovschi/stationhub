package com.stationhub.app;

import com.stationhub.command.Command;
import com.stationhub.io.JsonIO;
import com.stationhub.storage.InMemoryReadingStore;
import com.stationhub.time.SimulationClock;
import java.nio.file.Path;
import java.util.List;

public class Engine {

    public void run(Path input, Path output) {
        List<Command> commands = JsonIO.readCommands(input);
        EngineContext context = new EngineContext(new SimulationClock(), new InMemoryReadingStore());

        for (Command command : commands) {
            command.execute(context);
        }
        
        JsonIO.writeResponses(output, context.output());
    }
}

