package com.stationhub.response;

import java.time.LocalDateTime;

public class QueryDenied extends QueryResponse {

    private final String reason;

    public QueryDenied(LocalDateTime timestamp, LocalDateTime from, LocalDateTime to,
            String reason) {
        super(timestamp, Status.DENIED, from, to);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
