package com.stationhub.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;

public class QueryMissing extends QueryResponse {

    public QueryMissing(LocalDateTime timestamp, LocalDateTime from, LocalDateTime to) {
        super(timestamp, Status.MISSING, from, to);
    }

    @JsonInclude(JsonInclude.Include.ALWAYS)
    public List<DayResult> getDays() {
        return null;
    }

    public String getReason() {
        return "No reading yet. Try again later.";
    }
}
