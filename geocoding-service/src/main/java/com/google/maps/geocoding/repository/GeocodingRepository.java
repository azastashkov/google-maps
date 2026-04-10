package com.google.maps.geocoding.repository;

import com.google.maps.geocoding.model.GeocodingEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GeocodingRepository extends JpaRepository<GeocodingEntry, Long> {

    Optional<GeocodingEntry> findByAddressIgnoreCase(String address);
}
