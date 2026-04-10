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
public class RankedRoute {
    private int rank;
    private double distance;
    private double eta;
    private double score;
    private List<Coordinate> waypoints;
}
