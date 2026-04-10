package com.google.maps.geocoding.service;

import com.google.maps.geocoding.model.GeocodingEntry;
import com.google.maps.geocoding.repository.GeocodingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GeocodingService {

    private static final double EARTH_RADIUS_METERS = 6_371_000.0;
    private static final double THRESHOLD_METERS = 1000.0;

    private final GeocodingRepository repository;

    public Optional<GeocodingEntry> geocode(String address) {
        return repository.findByAddressIgnoreCase(address);
    }

    public Optional<GeocodingEntry> reverseGeocode(double lat, double lng) {
        List<GeocodingEntry> all = repository.findAll();
        return all.stream()
                .map(entry -> new EntryWithDistance(entry, haversineDistance(lat, lng, entry.getLatitude(), entry.getLongitude())))
                .filter(ewd -> ewd.distance() < THRESHOLD_METERS)
                .min(Comparator.comparingDouble(EntryWithDistance::distance))
                .map(EntryWithDistance::entry);
    }

    double haversineDistance(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_METERS * c;
    }

    private record EntryWithDistance(GeocodingEntry entry, double distance) {}
}
