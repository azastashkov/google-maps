package com.google.maps.routeplanner.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EtaRequest {
    private List<Coordinate> waypoints;
    private Instant departureTime;
}
