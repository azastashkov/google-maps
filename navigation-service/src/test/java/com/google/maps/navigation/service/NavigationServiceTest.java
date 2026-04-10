package com.google.maps.navigation.service;

import com.google.maps.navigation.client.GeocodingClient;
import com.google.maps.navigation.client.RoutePlannerClient;
import com.google.maps.navigation.dto.Coordinate;
import com.google.maps.navigation.dto.GeocodingResponse;
import com.google.maps.navigation.dto.NavigationResponse;
import com.google.maps.navigation.dto.RoutePlannerResponse;
import com.google.maps.navigation.dto.RouteResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NavigationServiceTest {

    @Mock
    private GeocodingClient geocodingClient;

    @Mock
    private RoutePlannerClient routePlannerClient;

    @InjectMocks
    private NavigationService navigationService;

    @Test
    void navigate_withAddresses_callsGeocodingFirst() {
        GeocodingResponse originGeo = GeocodingResponse.builder()
                .address("Origin St")
                .latitude(1.0)
                .longitude(2.0)
                .build();
        GeocodingResponse destGeo = GeocodingResponse.builder()
                .address("Dest St")
                .latitude(3.0)
                .longitude(4.0)
                .build();

        RouteResponse route = RouteResponse.builder()
                .rank(1)
                .distance(100.0)
                .eta(300.0)
                .waypoints(List.of())
                .build();
        RoutePlannerResponse plannerResponse = RoutePlannerResponse.builder()
                .routes(List.of(route))
                .build();

        when(geocodingClient.geocode("Origin St")).thenReturn(originGeo);
        when(geocodingClient.geocode("Dest St")).thenReturn(destGeo);
        when(routePlannerClient.getRoutes(anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyInt()))
                .thenReturn(plannerResponse);

        NavigationResponse result = navigationService.navigate("Origin St", "Dest St", null, null, null, null, 3);

        verify(geocodingClient, times(2)).geocode(anyString());
        verify(routePlannerClient).getRoutes(1.0, 2.0, 3.0, 4.0, 3);

        assertThat(result.getRoutes()).hasSize(1);
        assertThat(result.getRoutes().get(0).getRank()).isEqualTo(1);
    }

    @Test
    void navigate_withCoordinates_skipsGeocoding() {
        RouteResponse route = RouteResponse.builder()
                .rank(1)
                .distance(50.0)
                .eta(150.0)
                .waypoints(List.of(new Coordinate(1.0, 2.0)))
                .build();
        RoutePlannerResponse plannerResponse = RoutePlannerResponse.builder()
                .routes(List.of(route))
                .build();

        when(routePlannerClient.getRoutes(anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyInt()))
                .thenReturn(plannerResponse);

        NavigationResponse result = navigationService.navigate(null, null, 1.0, 2.0, 3.0, 4.0, 3);

        verifyNoInteractions(geocodingClient);
        verify(routePlannerClient).getRoutes(1.0, 2.0, 3.0, 4.0, 3);

        assertThat(result.getRoutes()).hasSize(1);
    }
}
