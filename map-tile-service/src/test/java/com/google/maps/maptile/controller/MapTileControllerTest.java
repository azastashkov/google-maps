package com.google.maps.maptile.controller;

import com.google.maps.maptile.dto.TileInfo;
import com.google.maps.maptile.dto.TileResponse;
import com.google.maps.maptile.service.MapTileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MapTileController.class)
class MapTileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MapTileService mapTileService;

    @Test
    void getTiles_returnsOkWithZoomAndTileId() throws Exception {
        TileResponse mockResponse = TileResponse.builder()
                .zoom(10)
                .tiles(List.of(
                        TileInfo.builder().tileId("10/512/384").x(512).y(384).build()
                ))
                .build();

        when(mapTileService.getTiles(anyDouble(), anyDouble(), anyInt(), anyInt(), anyInt()))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/tiles")
                        .param("lat", "40.7128")
                        .param("lng", "-74.0060")
                        .param("zoom", "10")
                        .param("viewportWidth", "800")
                        .param("viewportHeight", "600"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.zoom").value(10))
                .andExpect(jsonPath("$.tiles[0].tileId").value("10/512/384"));
    }
}
