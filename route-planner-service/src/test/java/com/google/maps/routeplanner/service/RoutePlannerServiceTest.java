package com.google.maps.routeplanner.service;

import com.google.maps.routeplanner.client.EtaClient;
import com.google.maps.routeplanner.client.RankerClient;
import com.google.maps.routeplanner.client.ShortestPathClient;
import com.google.maps.routeplanner.dto.EtaRequest;
import com.google.maps.routeplanner.dto.EtaResponse;
import com.google.maps.routeplanner.dto.PathResult;
import com.google.maps.routeplanner.dto.RankedRoute;
import com.google.maps.routeplanner.dto.RankerRequest;
import com.google.maps.routeplanner.dto.RankerResponse;
import com.google.maps.routeplanner.dto.RoutePlannerResponse;
import com.google.maps.routeplanner.dto.ShortestPathsResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoutePlannerServiceTest {

    @Mock
    private ShortestPathClient shortestPathClient;

    @Mock
    private EtaClient etaClient;

    @Mock
    private RankerClient rankerClient;

    @InjectMocks
    private RoutePlannerService routePlannerService;

    @Test
    void planRoutes_orchestratesAllThreeServices() {
        PathResult path = PathResult.builder()
                .distance(100.0)
                .waypoints(List.of())
                .build();
        ShortestPathsResponse shortestPathsResponse = ShortestPathsResponse.builder()
                .paths(List.of(path))
                .build();

        EtaResponse etaResponse = EtaResponse.builder()
                .estimatedSeconds(300.0)
                .trafficFactor(1.2)
                .build();

        RankedRoute rankedRoute = RankedRoute.builder()
                .rank(1)
                .distance(100.0)
                .eta(300.0)
                .score(0.0)
                .waypoints(List.of())
                .build();
        RankerResponse rankerResponse = RankerResponse.builder()
                .rankedRoutes(List.of(rankedRoute))
                .build();

        when(shortestPathClient.getShortestPaths(anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyInt()))
                .thenReturn(shortestPathsResponse);
        when(etaClient.getEta(any(EtaRequest.class))).thenReturn(etaResponse);
        when(rankerClient.rank(any(RankerRequest.class))).thenReturn(rankerResponse);

        RoutePlannerResponse result = routePlannerService.planRoutes(1.0, 2.0, 3.0, 4.0, 3);

        verify(shortestPathClient).getShortestPaths(1.0, 2.0, 3.0, 4.0, 3);
        verify(etaClient).getEta(any(EtaRequest.class));
        verify(rankerClient).rank(any(RankerRequest.class));

        assertThat(result.getRoutes()).hasSize(1);
        assertThat(result.getRoutes().get(0).getRank()).isEqualTo(1);
        assertThat(result.getRoutes().get(0).getDistance()).isEqualTo(100.0);
        assertThat(result.getRoutes().get(0).getEta()).isEqualTo(300.0);
    }
}
