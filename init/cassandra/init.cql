CREATE KEYSPACE IF NOT EXISTS google_maps WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

CREATE TABLE IF NOT EXISTS google_maps.user_locations (
    user_id text,
    timestamp timestamp,
    latitude double,
    longitude double,
    PRIMARY KEY (user_id, timestamp)
) WITH CLUSTERING ORDER BY (timestamp DESC);
