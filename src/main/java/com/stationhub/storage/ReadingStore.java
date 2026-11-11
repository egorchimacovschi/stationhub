package com.stationhub.storage;

import com.stationhub.domain.DataType;
import com.stationhub.domain.Reading;
import com.stationhub.time.Interval;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReadingStore {

    void save(Reading reading);

    Optional<Reading> find(DataType type, LocalDate date, Interval interval);

    List<Reading> range(DataType type, LocalDateTime from, LocalDateTime to);

    Optional<Reading> latest(DataType type);

    Optional<Reading> previousValid(Reading reading);
}
