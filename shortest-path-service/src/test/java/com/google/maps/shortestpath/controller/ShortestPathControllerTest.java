package com.google.maps.shortestpath.controller;

import com.google.maps.shortestpath.dto.Coordinate;
import com.google.maps.shortestpath.dto.PathResult;
import com.google.maps.shortestpath.dto.ShortestPathsResponse;
import com.google.maps.shortestpath.service.ShortestPathService;
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

@WebMvcTest(ShortestPathController.class)
class ShortestPathControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShortestPathService shortestPathService;

    @Test
    void getShortestPaths_returnsResults() throws Exception {
        List<Coordinate> waypoints = List.of(
                new Coordinate(40.70, -74.02),
                new Coordinate(40.705, -74.02),
                new Coordinate(40.71, -74.015)
        );
        PathResult pathResult = new PathResult(1234.5, waypoints);
        ShortestPathsResponse response = new ShortestPathsResponse(List.of(pathResult));

        when(shortestPathService.findShortestPaths(anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyInt()))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/shortest-paths")
                        .param("originLat", "40.70")
                        .param("originLng", "-74.02")
                        .param("destLat", "40.71")
                        .param("destLng", "-74.015")
                        .param("k", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths[0].distance").value(1234.5));
    }
}
