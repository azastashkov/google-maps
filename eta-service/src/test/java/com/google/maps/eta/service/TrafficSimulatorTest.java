package com.google.maps.eta.service;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class TrafficSimulatorTest {

    private final TrafficSimulator trafficSimulator = new TrafficSimulator();

    @Test
    void getTrafficMultiplier_earlyMorning_returns0_8() {
        Instant time = LocalDateTime.of(2024, 1, 1, 3, 0).toInstant(ZoneOffset.UTC);
        assertThat(trafficSimulator.getTrafficMultiplier(time)).isEqualTo(0.8);
    }

    @Test
    void getTrafficMultiplier_morningRush_returns1_5() {
        Instant time = LocalDateTime.of(2024, 1, 1, 7, 30).toInstant(ZoneOffset.UTC);
        assertThat(trafficSimulator.getTrafficMultiplier(time)).isEqualTo(1.5);
    }

    @Test
    void getTrafficMultiplier_midday_returns1_0() {
        Instant time = LocalDateTime.of(2024, 1, 1, 12, 0).toInstant(ZoneOffset.UTC);
        assertThat(trafficSimulator.getTrafficMultiplier(time)).isEqualTo(1.0);
    }

    @Test
    void getTrafficMultiplier_eveningRush_returns1_5() {
        Instant time = LocalDateTime.of(2024, 1, 1, 17, 0).toInstant(ZoneOffset.UTC);
        assertThat(trafficSimulator.getTrafficMultiplier(time)).isEqualTo(1.5);
    }

    @Test
    void getTrafficMultiplier_lateEvening_returns0_9() {
        Instant time = LocalDateTime.of(2024, 1, 1, 21, 0).toInstant(ZoneOffset.UTC);
        assertThat(trafficSimulator.getTrafficMultiplier(time)).isEqualTo(0.9);
    }
}
