package com.google.maps.maptile.service;

import com.google.maps.maptile.dto.TileInfo;
import com.google.maps.maptile.dto.TileResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class MapTileService {

    private static final int TILE_SIZE_PX = 256;

    public TileResponse getTiles(double lat, double lng, int zoom, int viewportWidth, int viewportHeight) {
        int n = 1 << zoom;

        int xCenter = (int) Math.floor((lng + 180.0) / 360.0 * n);
        double latRad = Math.toRadians(lat);
        int yCenter = (int) Math.floor((1.0 - Math.log(Math.tan(latRad) + 1.0 / Math.cos(latRad)) / Math.PI) / 2.0 * n);

        int tilesX = (int) Math.ceil(viewportWidth / (double) TILE_SIZE_PX);
        int tilesY = (int) Math.ceil(viewportHeight / (double) TILE_SIZE_PX);

        int halfX = tilesX / 2;
        int halfY = tilesY / 2;

        List<TileInfo> tiles = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        for (int dx = -halfX; dx <= halfX; dx++) {
            for (int dy = -halfY; dy <= halfY; dy++) {
                int rawX = xCenter + dx;
                int rawY = yCenter + dy;

                int clampedX = Math.max(0, Math.min(rawX, n - 1));
                int clampedY = Math.max(0, Math.min(rawY, n - 1));

                String tileId = zoom + "/" + clampedX + "/" + clampedY;
                if (seen.add(tileId)) {
                    tiles.add(TileInfo.builder()
                            .tileId(tileId)
                            .x(clampedX)
                            .y(clampedY)
                            .build());
                }
            }
        }

        return TileResponse.builder()
                .zoom(zoom)
                .tiles(tiles)
                .build();
    }
}
