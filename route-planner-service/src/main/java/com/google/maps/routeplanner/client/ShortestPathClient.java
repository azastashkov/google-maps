package com.google.maps.routeplanner.client;

import com.google.maps.routeplanner.dto.ShortestPathsResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ShortestPathClient {

    private final RestClient restClient;

    public ShortestPathClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public ShortestPathsResponse getShortestPaths(double originLat, double originLng, double destLat, double destLng, int k) {
        return restClient.get()
                .uri("http://shortest-path-service/api/v1/shortest-paths?originLat={originLat}&originLng={originLng}&destLat={destLat}&destLng={destLng}&k={k}",
                        originLat, originLng, destLat, destLng, k)
                .retrieve()
                .body(ShortestPathsResponse.class);
    }
}
