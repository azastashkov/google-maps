package com.google.maps.navigation.client;

import com.google.maps.navigation.dto.RoutePlannerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class RoutePlannerClient {

    private final RestTemplate restTemplate;

    public RoutePlannerResponse getRoutes(double originLat, double originLng, double destLat, double destLng, int k) {
        return restTemplate.getForObject(
                "http://route-planner-service/api/v1/routes?originLat={originLat}&originLng={originLng}&destLat={destLat}&destLng={destLng}&k={k}",
                RoutePlannerResponse.class,
                originLat, originLng, destLat, destLng, k);
    }
}
