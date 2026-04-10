package com.google.maps.location.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.maps.location.dto.LocationRequest;
import com.google.maps.location.model.UserLocation;
import com.google.maps.location.service.LocationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LocationController.class)
class LocationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LocationService locationService;

    @Test
    void putLocation_returns200() throws Exception {
        LocationRequest request = new LocationRequest(37.7749, -122.4194, Instant.now());

        mockMvc.perform(put("/api/v1/locations/user1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(locationService).updateLocation(eq("user1"), any(UserLocation.class));
    }

    @Test
    void getLocation_returnsLocationWhenExists() throws Exception {
        UserLocation location = UserLocation.builder()
                .userId("user1")
                .latitude(37.7749)
                .longitude(-122.4194)
                .timestamp(Instant.parse("2024-01-01T00:00:00Z"))
                .build();

        when(locationService.getLatestLocation("user1")).thenReturn(Optional.of(location));

        mockMvc.perform(get("/api/v1/locations/user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user1"))
                .andExpect(jsonPath("$.latitude").value(37.7749))
                .andExpect(jsonPath("$.longitude").value(-122.4194));
    }

    @Test
    void getLocation_returns404WhenNotExists() throws Exception {
        when(locationService.getLatestLocation("user1")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/locations/user1"))
                .andExpect(status().isNotFound());
    }
}
