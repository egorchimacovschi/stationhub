package com.stationhub.rules;

import java.util.Optional;

@FunctionalInterface
public interface Rule<T> {
    Optional<String> violation(T value);
}
