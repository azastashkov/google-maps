package com.google.maps.maptile.service;

import com.google.maps.maptile.dto.TileInfo;
import com.google.maps.maptile.dto.TileResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MapTileServiceTest {

    private final MapTileService mapTileService = new MapTileService();

    @Test
    void getTiles_nyc_zoom12_correctCenterTile() {
        double lat = 40.7128;
        double lng = -74.0060;
        int zoom = 12;

        TileResponse response = mapTileService.getTiles(lat, lng, zoom, 256, 256);

        assertThat(response.getZoom()).isEqualTo(zoom);
        assertThat(response.getTiles()).isNotEmpty();

        TileInfo centerTile = response.getTiles().get(0);
        assertThat(centerTile.getX()).isBetween(1200, 1210);
        assertThat(centerTile.getY()).isBetween(1535, 1545);
    }

    @Test
    void getTiles_zoom0_returnsSingleTile() {
        TileResponse response = mapTileService.getTiles(0.0, 0.0, 0, 256, 256);

        assertThat(response.getZoom()).isEqualTo(0);
        assertThat(response.getTiles()).hasSize(1);
        assertThat(response.getTiles().get(0).getTileId()).isEqualTo("0/0/0");
    }

    @Test
    void getTiles_largerViewport_returnsMoreTiles() {
        TileResponse smallResponse = mapTileService.getTiles(40.7128, -74.0060, 10, 256, 256);
        TileResponse largeResponse = mapTileService.getTiles(40.7128, -74.0060, 10, 800, 600);

        assertThat(largeResponse.getTiles().size()).isGreaterThan(smallResponse.getTiles().size());
    }

    @Test
    void getTiles_tilesClampedToValidRange() {
        TileResponse response = mapTileService.getTiles(85.0, 179.9, 2, 512, 512);

        int maxCoord = (1 << 2) - 1; // 3
        List<TileInfo> tiles = response.getTiles();
        assertThat(tiles).isNotEmpty();
        for (TileInfo tile : tiles) {
            assertThat(tile.getX()).isBetween(0, maxCoord);
            assertThat(tile.getY()).isBetween(0, maxCoord);
        }
    }
}
