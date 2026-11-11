package com.stationhub.rules;

import java.util.Optional;

public final class Rules {

    private Rules() {
    }

    public static Rule<Double> between(double min, double max) {
        return v -> v < min || v > max
                ? Optional.of("is outside the allowed range [" + fmt(min) + ", " + fmt(max) + "]")
                : Optional.empty();
    }

    public static Rule<Double> atLeast(double min) {
        return v -> v < min ? Optional.of("is below the minimum " + fmt(min)) : Optional.empty();
    }

    public static Rule<Double> positive() {
        return v -> v <= 0 ? Optional.of("must be positive") : Optional.empty();
    }

    @SafeVarargs
    public static Optional<String> field(String name, Double value, Rule<Double>... rules) {
        if (value == null) {
            return Optional.of(name + " is missing; reading skipped.");
        }

        for (Rule<Double> rule : rules) {
            Optional<String> bad = rule.violation(value);

            if (bad.isPresent()) {
                return Optional.of(name + " " + value + bad.get() + "; reading skipped.");
            }
        }

        return Optional.empty();
    }

    @SafeVarargs
    public static Optional<String> firstFailure(Optional<String>... results) {
        for (Optional<String> r : results) {
            if (r.isPresent()) {
                return r;
            }
        }
        return Optional.empty();
    }

    private static String fmt(double d) {
        return d == Math.rint(d) ? String.valueOf((long) d) : String.valueOf(d);
    }
}
