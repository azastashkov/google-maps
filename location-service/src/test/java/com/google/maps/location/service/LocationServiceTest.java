package com.google.maps.location.service;

import com.google.maps.location.model.UserLocation;
import com.google.maps.location.repository.UserLocationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocationServiceTest {

    @Mock
    private UserLocationRepository userLocationRepository;

    @InjectMocks
    private LocationService locationService;

    @Test
    void updateLocation_savesToRepository() {
        UserLocation location = UserLocation.builder()
                .latitude(37.7749)
                .longitude(-122.4194)
                .timestamp(Instant.now())
                .build();

        when(userLocationRepository.save(any(UserLocation.class))).thenReturn(location);

        locationService.updateLocation("user1", location);

        verify(userLocationRepository).save(location);
    }

    @Test
    void getLatestLocation_returnsLocationWhenExists() {
        UserLocation location = UserLocation.builder()
                .userId("user1")
                .latitude(37.7749)
                .longitude(-122.4194)
                .timestamp(Instant.now())
                .build();

        when(userLocationRepository.findFirstByUserId("user1")).thenReturn(Optional.of(location));

        Optional<UserLocation> result = locationService.getLatestLocation("user1");

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(location);
    }

    @Test
    void getLatestLocation_returnsEmptyWhenNotExists() {
        when(userLocationRepository.findFirstByUserId("user1")).thenReturn(Optional.empty());

        Optional<UserLocation> result = locationService.getLatestLocation("user1");

        assertThat(result).isEmpty();
    }
}
