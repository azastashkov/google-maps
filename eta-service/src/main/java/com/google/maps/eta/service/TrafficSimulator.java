package com.google.maps.eta.service;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneOffset;

@Component
public class TrafficSimulator {

    public double getTrafficMultiplier(Instant departureTime) {
        int hour = departureTime.atZone(ZoneOffset.UTC).getHour();
        if (hour < 6) return 0.8;
        if (hour < 9) return 1.5;
        if (hour < 16) return 1.0;
        if (hour < 19) return 1.5;
        return 0.9;
    }
}
