package com.google.maps.eta.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.maps.eta.dto.Coordinate;
import com.google.maps.eta.dto.EtaRequest;
import com.google.maps.eta.dto.EtaResponse;
import com.google.maps.eta.service.EtaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EtaController.class)
class EtaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EtaService etaService;

    @Test
    void calculateEta_returnsOkWithEstimatedSecondsAndTrafficFactor() throws Exception {
        EtaRequest request = new EtaRequest(
                List.of(
                        Coordinate.builder().latitude(37.0).longitude(-122.0).build(),
                        Coordinate.builder().latitude(37.01).longitude(-122.0).build()
                ),
                Instant.parse("2024-01-01T12:00:00Z")
        );

        EtaResponse response = EtaResponse.builder()
                .estimatedSeconds(100.0)
                .trafficFactor(1.0)
                .build();

        when(etaService.calculateEta(any(), any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/eta")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estimatedSeconds").value(100.0))
                .andExpect(jsonPath("$.trafficFactor").value(1.0));
    }
}
