package com.google.maps.navigation.client;

import com.google.maps.navigation.dto.GeocodingResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class GeocodingClient {

    private final RestTemplate restTemplate;

    public GeocodingClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public GeocodingResponse geocode(String address) {
        return restTemplate.getForObject(
                "http://geocoding-service/api/v1/geocode?address={address}",
                GeocodingResponse.class,
                address);
    }
}
