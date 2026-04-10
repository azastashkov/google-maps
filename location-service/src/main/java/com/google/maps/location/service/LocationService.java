package com.google.maps.location.service;

import com.google.maps.location.model.UserLocation;
import com.google.maps.location.repository.UserLocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final UserLocationRepository userLocationRepository;

    public void updateLocation(String userId, UserLocation location) {
        location.setUserId(userId);
        userLocationRepository.save(location);
    }

    public Optional<UserLocation> getLatestLocation(String userId) {
        return userLocationRepository.findFirstByUserId(userId);
    }
}
