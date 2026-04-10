package com.google.maps.ranker.service;

import com.google.maps.ranker.dto.RankedRoute;
import com.google.maps.ranker.dto.RankerResponse;
import com.google.maps.ranker.dto.RouteCandidate;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class RankerServiceTest {

    private final RankerService rankerService = new RankerService();

    @Test
    void rankRoutes_ranksLowerEtaHigher() {
        RouteCandidate fastRoute = RouteCandidate.builder()
                .distance(100.0).eta(60.0).waypoints(List.of()).build();
        RouteCandidate slowRoute = RouteCandidate.builder()
                .distance(100.0).eta(120.0).waypoints(List.of()).build();

        RankerResponse response = rankerService.rankRoutes(List.of(fastRoute, slowRoute), 2);

        List<RankedRoute> ranked = response.getRankedRoutes();
        assertThat(ranked).hasSize(2);
        assertThat(ranked.get(0).getRank()).isEqualTo(1);
        assertThat(ranked.get(0).getEta()).isEqualTo(60.0);
        assertThat(ranked.get(1).getRank()).isEqualTo(2);
        assertThat(ranked.get(1).getEta()).isEqualTo(120.0);
    }

    @Test
    void rankRoutes_respectsWeighting() {
        // Low ETA, high distance
        RouteCandidate lowEtaHighDist = RouteCandidate.builder()
                .distance(200.0).eta(60.0).waypoints(List.of()).build();
        // High ETA, low distance
        RouteCandidate highEtaLowDist = RouteCandidate.builder()
                .distance(100.0).eta(120.0).waypoints(List.of()).build();

        RankerResponse response = rankerService.rankRoutes(List.of(lowEtaHighDist, highEtaLowDist), 2);

        List<RankedRoute> ranked = response.getRankedRoutes();
        // Low ETA should win because ETA weight (0.7) > distance weight (0.3)
        // lowEtaHighDist: normEta=0, normDist=1, score = 0.7*0 + 0.3*1 = 0.3
        // highEtaLowDist: normEta=1, normDist=0, score = 0.7*1 + 0.3*0 = 0.7
        assertThat(ranked.get(0).getEta()).isEqualTo(60.0);
        assertThat(ranked.get(0).getScore()).isCloseTo(0.3, within(0.001));
        assertThat(ranked.get(1).getEta()).isEqualTo(120.0);
        assertThat(ranked.get(1).getScore()).isCloseTo(0.7, within(0.001));
    }

    @Test
    void rankRoutes_singleRoute_returnsRank1WithScoreZero() {
        RouteCandidate singleRoute = RouteCandidate.builder()
                .distance(150.0).eta(90.0).waypoints(List.of()).build();

        RankerResponse response = rankerService.rankRoutes(List.of(singleRoute), 1);

        List<RankedRoute> ranked = response.getRankedRoutes();
        assertThat(ranked).hasSize(1);
        assertThat(ranked.get(0).getRank()).isEqualTo(1);
        assertThat(ranked.get(0).getScore()).isEqualTo(0.0);
    }

    @Test
    void rankRoutes_returnsOnlyTopK() {
        RouteCandidate route1 = RouteCandidate.builder()
                .distance(100.0).eta(60.0).waypoints(List.of()).build();
        RouteCandidate route2 = RouteCandidate.builder()
                .distance(150.0).eta(90.0).waypoints(List.of()).build();
        RouteCandidate route3 = RouteCandidate.builder()
                .distance(200.0).eta(120.0).waypoints(List.of()).build();

        RankerResponse response = rankerService.rankRoutes(List.of(route1, route2, route3), 2);

        assertThat(response.getRankedRoutes()).hasSize(2);
    }
}
