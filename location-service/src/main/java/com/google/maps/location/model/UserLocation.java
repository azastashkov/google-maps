package com.google.maps.location.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("user_locations")
public class UserLocation {

    @PrimaryKeyColumn(name = "user_id", type = PrimaryKeyType.PARTITIONED)
    private String userId;

    @PrimaryKeyColumn(name = "timestamp", type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    private Instant timestamp;

    @Column("latitude")
    private double latitude;

    @Column("longitude")
    private double longitude;
}
