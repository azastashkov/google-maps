package com.google.maps.shortestpath.controller;

import com.google.maps.shortestpath.dto.ShortestPathsResponse;
import com.google.maps.shortestpath.service.ShortestPathService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/shortest-paths")
@RequiredArgsConstructor
public class ShortestPathController {

    private final ShortestPathService shortestPathService;

    @GetMapping
    public ShortestPathsResponse getShortestPaths(
            @RequestParam double originLat,
            @RequestParam double originLng,
            @RequestParam double destLat,
            @RequestParam double destLng,
            @RequestParam(defaultValue = "3") int k) {
        return shortestPathService.findShortestPaths(originLat, originLng, destLat, destLng, k);
    }
}
