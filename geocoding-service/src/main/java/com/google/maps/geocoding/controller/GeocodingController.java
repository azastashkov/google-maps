package com.google.maps.geocoding.controller;

import com.google.maps.geocoding.dto.GeocodingResponse;
import com.google.maps.geocoding.model.GeocodingEntry;
import com.google.maps.geocoding.service.GeocodingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class GeocodingController {

    private final GeocodingService geocodingService;

    @GetMapping("/geocode")
    public ResponseEntity<GeocodingResponse> geocode(@RequestParam String address) {
        return geocodingService.geocode(address)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/reverse-geocode")
    public ResponseEntity<GeocodingResponse> reverseGeocode(
            @RequestParam double lat,
            @RequestParam double lng) {
        return geocodingService.reverseGeocode(lat, lng)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private GeocodingResponse toResponse(GeocodingEntry entry) {
        return GeocodingResponse.builder()
                .address(entry.getAddress())
                .latitude(entry.getLatitude())
                .longitude(entry.getLongitude())
                .build();
    }
}
