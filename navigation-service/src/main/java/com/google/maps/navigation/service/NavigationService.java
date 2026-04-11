package com.google.maps.navigation.service;

import com.google.maps.navigation.client.GeocodingClient;
import com.google.maps.navigation.client.RoutePlannerClient;
import com.google.maps.navigation.dto.GeocodingResponse;
import com.google.maps.navigation.dto.NavigationResponse;
import com.google.maps.navigation.dto.RoutePlannerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NavigationService {

    private final GeocodingClient geocodingClient;
    private final RoutePlannerClient routePlannerClient;

    public NavigationResponse navigate(String originAddress, String destinationAddress,
                                       Double originLat, Double originLng,
                                       Double destLat, Double destLng, int k) {
        if (originAddress != null && destinationAddress != null) {
            GeocodingResponse origin = geocodingClient.geocode(originAddress);
            GeocodingResponse dest = geocodingClient.geocode(destinationAddress);
            originLat = origin.getLatitude();
            originLng = origin.getLongitude();
            destLat = dest.getLatitude();
            destLng = dest.getLongitude();
        }
        RoutePlannerResponse planned = routePlannerClient.getRoutes(originLat, originLng, destLat, destLng, k);
        return NavigationResponse.builder().routes(planned.getRoutes()).build();
    }
}
