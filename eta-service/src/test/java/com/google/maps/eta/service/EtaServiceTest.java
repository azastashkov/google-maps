package com.google.maps.eta.service;

import com.google.maps.eta.dto.Coordinate;
import com.google.maps.eta.dto.EtaResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EtaServiceTest {

    @Mock
    private TrafficSimulator trafficSimulator;

    @InjectMocks
    private EtaService etaService;

    @Test
    void calculateEta_twoPoints_returnsCorrectEstimate() {
        // 0.01 degrees of latitude ~ 1111 meters
        List<Coordinate> waypoints = List.of(
                Coordinate.builder().latitude(37.0).longitude(-122.0).build(),
                Coordinate.builder().latitude(37.01).longitude(-122.0).build()
        );
        Instant departureTime = Instant.now();
        when(trafficSimulator.getTrafficMultiplier(departureTime)).thenReturn(1.5);

        EtaResponse response = etaService.calculateEta(waypoints, departureTime);

        assertThat(response.getEstimatedSeconds()).isCloseTo(150.0, within(10.0));
        assertThat(response.getTrafficFactor()).isEqualTo(1.5);
    }

    @Test
    void calculateEta_singleWaypoint_returnsZeroSeconds() {
        List<Coordinate> waypoints = List.of(
                Coordinate.builder().latitude(37.0).longitude(-122.0).build()
        );
        Instant departureTime = Instant.now();
        when(trafficSimulator.getTrafficMultiplier(departureTime)).thenReturn(1.0);

        EtaResponse response = etaService.calculateEta(waypoints, departureTime);

        assertThat(response.getEstimatedSeconds()).isEqualTo(0.0);
    }
}
