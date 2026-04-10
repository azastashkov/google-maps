package com.google.maps.routeplanner.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RouteResponse {
    private int rank;
    private double distance;
    private double eta;
    private List<Coordinate> waypoints;
}
