package com.google.maps.navigation.client;

import com.google.maps.navigation.dto.GeocodingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class GeocodingClient {

    private final RestTemplate restTemplate;

    public GeocodingResponse geocode(String address) {
        return restTemplate.getForObject(
                "http://geocoding-service/api/v1/geocode?address={address}",
                GeocodingResponse.class,
                address);
    }
}
