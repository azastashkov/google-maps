package com.google.maps.location.controller;

import com.google.maps.location.dto.LocationRequest;
import com.google.maps.location.model.UserLocation;
import com.google.maps.location.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @PutMapping("/{userId}")
    public ResponseEntity<Void> updateLocation(@PathVariable String userId,
                                               @RequestBody LocationRequest request) {
        UserLocation location = UserLocation.builder()
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .timestamp(request.getTimestamp())
                .build();
        locationService.updateLocation(userId, location);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserLocation> getLocation(@PathVariable String userId) {
        Optional<UserLocation> location = locationService.getLatestLocation(userId);
        return location.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
