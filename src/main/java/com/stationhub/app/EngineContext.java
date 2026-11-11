package com.stationhub.app;

import com.stationhub.response.Response;
import com.stationhub.storage.ReadingStore;
import com.stationhub.time.SimulationClock;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EngineContext {

    private final SimulationClock clock;
    private final ReadingStore store;
    private final List<Response> output = new ArrayList<>();
    private boolean feedActive;

    public EngineContext(SimulationClock clock, ReadingStore store) {
        this.clock = clock;
        this.store = store;
    }

    public SimulationClock clock() {
        return clock;
    }

    public ReadingStore store() {
        return store;
    }

    public boolean isFeedActive() {
        return feedActive;
    }

    public void setFeedActive(boolean feedActive) {
        this.feedActive = feedActive;
    }

    public void emit(Response response) {
        output.add(response);
    }

    public List<Response> output() {
        return Collections.unmodifiableList(output);
    }
}
