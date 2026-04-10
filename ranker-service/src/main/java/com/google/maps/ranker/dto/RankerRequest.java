package com.google.maps.ranker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RankerRequest {
    private List<RouteCandidate> routes;
    private int k;
}
