package com.google.maps.ranker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.maps.ranker.dto.RankedRoute;
import com.google.maps.ranker.dto.RankerRequest;
import com.google.maps.ranker.dto.RankerResponse;
import com.google.maps.ranker.dto.RouteCandidate;
import com.google.maps.ranker.service.RankerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RankerController.class)
class RankerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RankerService rankerService;

    @Test
    void rank_returnsRankedResults() throws Exception {
        RankerRequest request = new RankerRequest(
                List.of(
                        RouteCandidate.builder().distance(100.0).eta(60.0).waypoints(List.of()).build(),
                        RouteCandidate.builder().distance(200.0).eta(120.0).waypoints(List.of()).build()
                ),
                2
        );

        RankerResponse response = RankerResponse.builder()
                .rankedRoutes(List.of(
                        RankedRoute.builder().rank(1).distance(100.0).eta(60.0).score(0.0).waypoints(List.of()).build(),
                        RankedRoute.builder().rank(2).distance(200.0).eta(120.0).score(1.0).waypoints(List.of()).build()
                ))
                .build();

        when(rankerService.rankRoutes(any(), anyInt())).thenReturn(response);

        mockMvc.perform(post("/api/v1/rank")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rankedRoutes[0].rank").value(1))
                .andExpect(jsonPath("$.rankedRoutes[0].eta").value(60.0))
                .andExpect(jsonPath("$.rankedRoutes[1].rank").value(2))
                .andExpect(jsonPath("$.rankedRoutes[1].eta").value(120.0));
    }
}
