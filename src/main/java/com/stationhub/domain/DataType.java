package com.stationhub.domain;

import com.stationhub.exception.AccessDeniedException;

public enum DataType {
    TEMPERATURE(false),
    TEMPERATURE_FORECAST(true),
    AIR_QUALITY(false),
    AIR_QUALITY_FORECAST(false),
    WIND(false),
    PRECIPITATION(false);

    private final boolean proOnly;

    DataType(boolean proOnly) {
        this.proOnly = proOnly;
    }

    public boolean isProOnly() {
        return proOnly;
    }

    public void requireReadableBy(ClientTier tier) {
        if (proOnly && tier != ClientTier.PRO) {
            throw new AccessDeniedException("Access denied: " + name() + " required a PRO plani.");
        }
    }
}
