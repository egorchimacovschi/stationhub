package com.stationhub.io;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class TimeFormats {

    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    private TimeFormats() {
    }

    public static String format(LocalDateTime time) {
        return DATE_TIME.format(time);
    }
}
