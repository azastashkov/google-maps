package com.google.maps.routeplanner.controller;

import com.google.maps.routeplanner.dto.RoutePlannerResponse;
import com.google.maps.routeplanner.service.RoutePlannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RoutePlannerController {

    private final RoutePlannerService routePlannerService;

    @GetMapping("/routes")
    public RoutePlannerResponse getRoutes(
            @RequestParam double originLat,
            @RequestParam double originLng,
            @RequestParam double destLat,
            @RequestParam double destLng,
            @RequestParam(defaultValue = "3") int k) {
        return routePlannerService.planRoutes(originLat, originLng, destLat, destLng, k);
    }
}
