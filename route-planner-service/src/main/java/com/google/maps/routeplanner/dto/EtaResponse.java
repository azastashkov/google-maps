package com.google.maps.routeplanner.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EtaResponse {
    private double estimatedSeconds;
    private double trafficFactor;
}
