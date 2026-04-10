package com.google.maps.navigation.client;

import com.google.maps.navigation.dto.GeocodingResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class GeocodingClient {

    private final RestClient restClient;

    public GeocodingClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public GeocodingResponse geocode(String address) {
        return restClient.get()
                .uri("http://geocoding-service/api/v1/geocode?address={address}", address)
                .retrieve()
                .body(GeocodingResponse.class);
    }
}
