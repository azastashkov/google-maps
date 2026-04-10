package com.google.maps.location.repository;

import com.google.maps.location.model.UserLocation;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.Optional;

public interface UserLocationRepository extends CassandraRepository<UserLocation, String> {

    Optional<UserLocation> findFirstByUserId(String userId);
}
