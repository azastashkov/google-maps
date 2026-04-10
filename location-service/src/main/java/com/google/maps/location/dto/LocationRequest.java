package com.google.maps.location.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationRequest {

    private double latitude;
    private double longitude;
    private Instant timestamp;
}
