package com.google.maps.navigation.client;

import com.google.maps.navigation.dto.RoutePlannerResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class RoutePlannerClient {

    private final RestClient restClient;

    public RoutePlannerClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public RoutePlannerResponse getRoutes(double originLat, double originLng, double destLat, double destLng, int k) {
        return restClient.get()
                .uri("http://route-planner-service/api/v1/routes?originLat={originLat}&originLng={originLng}&destLat={destLat}&destLng={destLng}&k={k}",
                        originLat, originLng, destLat, destLng, k)
                .retrieve()
                .body(RoutePlannerResponse.class);
    }
}
