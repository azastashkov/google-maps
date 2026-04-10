package com.google.maps.geocoding.service;

import com.google.maps.geocoding.model.GeocodingEntry;
import com.google.maps.geocoding.repository.GeocodingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GeocodingServiceTest {

    @Mock
    private GeocodingRepository repository;

    @InjectMocks
    private GeocodingService geocodingService;

    @Test
    void geocode_returnsEntryWhenAddressFound() {
        GeocodingEntry entry = GeocodingEntry.builder()
                .id(1L)
                .address("1600 Amphitheatre Parkway, Mountain View, CA")
                .latitude(37.4220)
                .longitude(-122.0841)
                .build();
        when(repository.findByAddressIgnoreCase("1600 Amphitheatre Parkway, Mountain View, CA"))
                .thenReturn(Optional.of(entry));

        Optional<GeocodingEntry> result = geocodingService.geocode("1600 Amphitheatre Parkway, Mountain View, CA");

        assertThat(result).isPresent();
        assertThat(result.get().getAddress()).isEqualTo("1600 Amphitheatre Parkway, Mountain View, CA");
        assertThat(result.get().getLatitude()).isEqualTo(37.4220);
        assertThat(result.get().getLongitude()).isEqualTo(-122.0841);
    }

    @Test
    void geocode_returnsEmptyWhenAddressNotFound() {
        when(repository.findByAddressIgnoreCase("Unknown Address")).thenReturn(Optional.empty());

        Optional<GeocodingEntry> result = geocodingService.geocode("Unknown Address");

        assertThat(result).isEmpty();
    }

    @Test
    void reverseGeocode_returnsNearestEntryWithinThreshold() {
        GeocodingEntry nearby = GeocodingEntry.builder()
                .id(1L)
                .address("Nearby Place")
                .latitude(37.4220)
                .longitude(-122.0841)
                .build();
        GeocodingEntry farEntry = GeocodingEntry.builder()
                .id(2L)
                .address("Far Place")
                .latitude(37.5000)
                .longitude(-122.5000)
                .build();
        when(repository.findAll()).thenReturn(List.of(nearby, farEntry));

        // Query point very close to nearby (within a few meters)
        Optional<GeocodingEntry> result = geocodingService.reverseGeocode(37.4221, -122.0841);

        assertThat(result).isPresent();
        assertThat(result.get().getAddress()).isEqualTo("Nearby Place");
    }

    @Test
    void reverseGeocode_returnsEmptyWhenNothingWithinThreshold() {
        GeocodingEntry entry = GeocodingEntry.builder()
                .id(1L)
                .address("Some Place")
                .latitude(37.4220)
                .longitude(-122.0841)
                .build();
        when(repository.findAll()).thenReturn(List.of(entry));

        // Query point far away (e.g., New York vs California)
        Optional<GeocodingEntry> result = geocodingService.reverseGeocode(40.7128, -74.0060);

        assertThat(result).isEmpty();
    }

    @Test
    void haversineDistance_computesCorrectly() {
        // 0.01 degrees of latitude ~ 1111 meters
        double distance = geocodingService.haversineDistance(37.0, -122.0, 37.01, -122.0);

        assertThat(distance).isCloseTo(1111.0, within(50.0));
    }
}
