package com.google.maps.routeplanner.service;

import com.google.maps.routeplanner.client.EtaClient;
import com.google.maps.routeplanner.client.RankerClient;
import com.google.maps.routeplanner.client.ShortestPathClient;
import com.google.maps.routeplanner.dto.EtaRequest;
import com.google.maps.routeplanner.dto.EtaResponse;
import com.google.maps.routeplanner.dto.RankerRequest;
import com.google.maps.routeplanner.dto.RankerResponse;
import com.google.maps.routeplanner.dto.RouteCandidate;
import com.google.maps.routeplanner.dto.RoutePlannerResponse;
import com.google.maps.routeplanner.dto.RouteResponse;
import com.google.maps.routeplanner.dto.ShortestPathsResponse;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class RoutePlannerService {

    private final ShortestPathClient shortestPathClient;
    private final EtaClient etaClient;
    private final RankerClient rankerClient;

    public RoutePlannerService(ShortestPathClient shortestPathClient, EtaClient etaClient, RankerClient rankerClient) {
        this.shortestPathClient = shortestPathClient;
        this.etaClient = etaClient;
        this.rankerClient = rankerClient;
    }

    public RoutePlannerResponse planRoutes(double originLat, double originLng, double destLat, double destLng, int k) {
        ShortestPathsResponse paths = shortestPathClient.getShortestPaths(originLat, originLng, destLat, destLng, k);

        List<RouteCandidate> candidates = paths.getPaths().stream()
                .map(path -> {
                    EtaResponse eta = etaClient.getEta(new EtaRequest(path.getWaypoints(), Instant.now()));
                    return RouteCandidate.builder()
                            .distance(path.getDistance())
                            .eta(eta.getEstimatedSeconds())
                            .waypoints(path.getWaypoints())
                            .build();
                }).toList();

        RankerResponse ranked = rankerClient.rank(new RankerRequest(candidates, k));

        List<RouteResponse> routes = ranked.getRankedRoutes().stream()
                .map(r -> RouteResponse.builder()
                        .rank(r.getRank())
                        .distance(r.getDistance())
                        .eta(r.getEta())
                        .waypoints(r.getWaypoints())
                        .build())
                .toList();

        return RoutePlannerResponse.builder().routes(routes).build();
    }
}
