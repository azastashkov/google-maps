package com.google.maps.eta.service;

import com.google.maps.eta.dto.Coordinate;
import com.google.maps.eta.dto.EtaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EtaService {

    private static final double BASE_SPEED_MS = 40.0 * 1000.0 / 3600.0;
    private static final double EARTH_RADIUS_METERS = 6_371_000.0;

    private final TrafficSimulator trafficSimulator;

    public EtaResponse calculateEta(List<Coordinate> waypoints, Instant departureTime) {
        double totalDistance = 0.0;
        for (int i = 0; i < waypoints.size() - 1; i++) {
            Coordinate from = waypoints.get(i);
            Coordinate to = waypoints.get(i + 1);
            totalDistance += haversineDistance(from.getLatitude(), from.getLongitude(),
                    to.getLatitude(), to.getLongitude());
        }

        double baseTravelTime = totalDistance / BASE_SPEED_MS;
        double trafficFactor = trafficSimulator.getTrafficMultiplier(departureTime);
        double estimatedSeconds = baseTravelTime * trafficFactor;

        return EtaResponse.builder()
                .estimatedSeconds(estimatedSeconds)
                .trafficFactor(trafficFactor)
                .build();
    }

    private double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_METERS * c;
    }
}
