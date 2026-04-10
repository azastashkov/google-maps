package com.google.maps.routeplanner.client;

import com.google.maps.routeplanner.dto.ShortestPathsResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ShortestPathClient {

    private final RestTemplate restTemplate;

    public ShortestPathClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ShortestPathsResponse getShortestPaths(double originLat, double originLng, double destLat, double destLng, int k) {
        return restTemplate.getForObject(
                "http://shortest-path-service/api/v1/shortest-paths?originLat={originLat}&originLng={originLng}&destLat={destLat}&destLng={destLng}&k={k}",
                ShortestPathsResponse.class,
                originLat, originLng, destLat, destLng, k);
    }
}
