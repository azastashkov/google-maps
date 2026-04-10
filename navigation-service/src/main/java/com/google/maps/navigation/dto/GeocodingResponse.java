package com.google.maps.navigation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GeocodingResponse {
    private String address;
    private double latitude;
    private double longitude;
}
