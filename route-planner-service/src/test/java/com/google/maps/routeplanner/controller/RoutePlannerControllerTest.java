package com.google.maps.routeplanner.controller;

import com.google.maps.routeplanner.dto.RouteResponse;
import com.google.maps.routeplanner.dto.RoutePlannerResponse;
import com.google.maps.routeplanner.service.RoutePlannerService;
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

@WebMvcTest(RoutePlannerController.class)
class RoutePlannerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoutePlannerService routePlannerService;

    @Test
    void getRoutes_returns200WithRoutes() throws Exception {
        RouteResponse route = RouteResponse.builder()
                .rank(1)
                .distance(100.0)
                .eta(300.0)
                .waypoints(List.of())
                .build();
        RoutePlannerResponse response = RoutePlannerResponse.builder()
                .routes(List.of(route))
                .build();

        when(routePlannerService.planRoutes(anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyInt()))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/routes")
                        .param("originLat", "1.0")
                        .param("originLng", "2.0")
                        .param("destLat", "3.0")
                        .param("destLng", "4.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.routes[0].rank").value(1));
    }
}
