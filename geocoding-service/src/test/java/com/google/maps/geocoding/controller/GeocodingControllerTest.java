package com.google.maps.geocoding.controller;

import com.google.maps.geocoding.model.GeocodingEntry;
import com.google.maps.geocoding.service.GeocodingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GeocodingController.class)
class GeocodingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GeocodingService geocodingService;

    @Test
    void geocode_returnsResult() throws Exception {
        GeocodingEntry entry = GeocodingEntry.builder()
                .id(1L)
                .address("1600 Amphitheatre Parkway, Mountain View, CA")
                .latitude(37.4220)
                .longitude(-122.0841)
                .build();
        when(geocodingService.geocode("1600 Amphitheatre Parkway, Mountain View, CA"))
                .thenReturn(Optional.of(entry));

        mockMvc.perform(get("/api/v1/geocode")
                        .param("address", "1600 Amphitheatre Parkway, Mountain View, CA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value("1600 Amphitheatre Parkway, Mountain View, CA"))
                .andExpect(jsonPath("$.latitude").value(37.4220))
                .andExpect(jsonPath("$.longitude").value(-122.0841));
    }

    @Test
    void geocode_returns404WhenNotFound() throws Exception {
        when(geocodingService.geocode("Unknown Address")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/geocode")
                        .param("address", "Unknown Address"))
                .andExpect(status().isNotFound());
    }

    @Test
    void reverseGeocode_returnsResult() throws Exception {
        GeocodingEntry entry = GeocodingEntry.builder()
                .id(1L)
                .address("Nearby Place")
                .latitude(37.4220)
                .longitude(-122.0841)
                .build();
        when(geocodingService.reverseGeocode(37.4220, -122.0841)).thenReturn(Optional.of(entry));

        mockMvc.perform(get("/api/v1/reverse-geocode")
                        .param("lat", "37.422")
                        .param("lng", "-122.0841"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value("Nearby Place"))
                .andExpect(jsonPath("$.latitude").value(37.4220))
                .andExpect(jsonPath("$.longitude").value(-122.0841));
    }

    @Test
    void reverseGeocode_returns404WhenNotInRange() throws Exception {
        when(geocodingService.reverseGeocode(40.7128, -74.0060)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/reverse-geocode")
                        .param("lat", "40.7128")
                        .param("lng", "-74.006"))
                .andExpect(status().isNotFound());
    }
}
