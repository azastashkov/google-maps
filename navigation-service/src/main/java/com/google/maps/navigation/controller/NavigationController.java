package com.google.maps.navigation.controller;

import com.google.maps.navigation.dto.NavigationResponse;
import com.google.maps.navigation.service.NavigationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class NavigationController {

    private final NavigationService navigationService;

    public NavigationController(NavigationService navigationService) {
        this.navigationService = navigationService;
    }

    @GetMapping("/navigate")
    public ResponseEntity<NavigationResponse> navigate(
            @RequestParam(required = false) String originAddress,
            @RequestParam(required = false) String destinationAddress,
            @RequestParam(required = false) Double originLat,
            @RequestParam(required = false) Double originLng,
            @RequestParam(required = false) Double destLat,
            @RequestParam(required = false) Double destLng,
            @RequestParam(defaultValue = "3") int k) {

        boolean hasAddresses = originAddress != null && destinationAddress != null;
        boolean hasCoordinates = originLat != null && originLng != null && destLat != null && destLng != null;

        if (!hasAddresses && !hasCoordinates) {
            return ResponseEntity.badRequest().build();
        }

        NavigationResponse response = navigationService.navigate(
                originAddress, destinationAddress, originLat, originLng, destLat, destLng, k);
        return ResponseEntity.ok(response);
    }
}
