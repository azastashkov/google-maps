package com.google.maps.ranker.service;

import com.google.maps.ranker.dto.RankedRoute;
import com.google.maps.ranker.dto.RankerResponse;
import com.google.maps.ranker.dto.RouteCandidate;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RankerService {

    public RankerResponse rankRoutes(List<RouteCandidate> routes, int k) {
        double minEta = routes.stream().mapToDouble(RouteCandidate::getEta).min().orElse(0);
        double maxEta = routes.stream().mapToDouble(RouteCandidate::getEta).max().orElse(0);
        double minDist = routes.stream().mapToDouble(RouteCandidate::getDistance).min().orElse(0);
        double maxDist = routes.stream().mapToDouble(RouteCandidate::getDistance).max().orElse(0);
        double etaRange = maxEta - minEta;
        double distRange = maxDist - minDist;

        AtomicInteger rankCounter = new AtomicInteger(1);

        List<RankedRoute> ranked = routes.stream()
                .map(route -> {
                    double normEta = etaRange > 0 ? (route.getEta() - minEta) / etaRange : 0;
                    double normDist = distRange > 0 ? (route.getDistance() - minDist) / distRange : 0;
                    double score = 0.7 * normEta + 0.3 * normDist;
                    return RankedRoute.builder()
                            .distance(route.getDistance()).eta(route.getEta())
                            .score(score).waypoints(route.getWaypoints()).build();
                })
                .sorted(Comparator.comparingDouble(RankedRoute::getScore))
                .limit(k)
                .peek(r -> r.setRank(rankCounter.getAndIncrement()))
                .toList();
        return RankerResponse.builder().rankedRoutes(ranked).build();
    }
}
