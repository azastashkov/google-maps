package com.google.maps.maptile.controller;

import com.google.maps.maptile.dto.TileResponse;
import com.google.maps.maptile.service.MapTileService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tiles")
public class MapTileController {

    private final MapTileService mapTileService;

    public MapTileController(MapTileService mapTileService) {
        this.mapTileService = mapTileService;
    }

    @GetMapping
    public TileResponse getTiles(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam int zoom,
            @RequestParam int viewportWidth,
            @RequestParam int viewportHeight) {
        return mapTileService.getTiles(lat, lng, zoom, viewportWidth, viewportHeight);
    }
}
