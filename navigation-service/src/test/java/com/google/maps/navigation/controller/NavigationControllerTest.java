package com.google.maps.navigation.controller;

import com.google.maps.navigation.dto.NavigationResponse;
import com.google.maps.navigation.dto.RouteResponse;
import com.google.maps.navigation.service.NavigationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NavigationController.class)
class NavigationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NavigationService navigationService;

    @Test
    void navigate_withAddresses_returns200() throws Exception {
        RouteResponse route = RouteResponse.builder()
                .rank(1)
                .distance(100.0)
                .eta(300.0)
                .waypoints(List.of())
                .build();
        NavigationResponse response = NavigationResponse.builder()
                .routes(List.of(route))
                .build();

        when(navigationService.navigate(anyString(), anyString(), isNull(), isNull(), isNull(), isNull(), anyInt()))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/navigate")
                        .param("originAddress", "Origin St")
                        .param("destinationAddress", "Dest St"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.routes[0].rank").value(1));
    }

    @Test
    void navigate_withCoords_returns200() throws Exception {
        RouteResponse route = RouteResponse.builder()
                .rank(1)
                .distance(50.0)
                .eta(150.0)
                .waypoints(List.of())
                .build();
        NavigationResponse response = NavigationResponse.builder()
                .routes(List.of(route))
                .build();

        when(navigationService.navigate(isNull(), isNull(), anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyInt()))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/navigate")
                        .param("originLat", "1.0")
                        .param("originLng", "2.0")
                        .param("destLat", "3.0")
                        .param("destLng", "4.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.routes[0].rank").value(1));
    }

    @Test
    void navigate_missingParams_returns400() throws Exception {
        mockMvc.perform(get("/api/v1/navigate"))
                .andExpect(status().isBadRequest());
    }
}
