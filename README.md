# Google Maps Simplified

A simplified, microservice-based Google Maps clone built with Java 21 and Spring Boot. The system provides location tracking, geocoding, route planning, and map tile serving, all behind an Nginx API gateway with ZooKeeper service discovery and Prometheus/Grafana monitoring.

## Architecture

![Components Diagram](docs/google-maps-components.drawio)

### Data Flow

1. The **Client** sends all requests to **Nginx** (port 80), which acts as the API gateway and load balances across two instances of each service.
2. **location-service** stores and retrieves user GPS coordinates from **Cassandra**.
3. **navigation-service** resolves human-readable addresses via **geocoding-service**, then delegates route computation to **route-planner-service**.
4. **route-planner-service** orchestrates three downstream services:
   - **shortest-path-service** — computes candidate graph paths (Dijkstra/Yen's k-shortest)
   - **eta-service** — estimates travel time for each candidate route
   - **ranker-service** — scores and ranks the candidates, returning the top-k routes
5. **geocoding-service** reads address↔coordinate mappings from **PostgreSQL**.
6. **map-tile-service** returns tile metadata for a given viewport.
7. All services register themselves with **ZooKeeper** for service discovery and expose `/actuator/prometheus` endpoints that **Prometheus** scrapes. **Grafana** queries Prometheus to render dashboards.

## Services

| Service | Port | Database | Description |
|---|---|---|---|
| location-service | 8081 | Cassandra | Stores and retrieves real-time user GPS coordinates |
| navigation-service | 8082 | — | Orchestrates end-to-end navigation: geocoding + route planning |
| geocoding-service | 8083 | PostgreSQL | Converts addresses to coordinates and vice versa |
| route-planner-service | 8084 | — | Coordinates shortest-path, ETA, and ranking to produce ranked routes |
| shortest-path-service | 8085 | — | Computes k-shortest paths between two geographic points |
| eta-service | 8086 | — | Estimates travel time for a sequence of waypoints |
| ranker-service | 8087 | — | Ranks a list of route candidates and returns the top-k |
| map-tile-service | 8088 | — | Returns map tile metadata for a given center coordinate and zoom level |

## Infrastructure

| Component | Port | Purpose |
|---|---|---|
| Nginx | 80 | API gateway and load balancer |
| ZooKeeper | 2181 | Service registry and discovery |
| ZooNavigator | 9000 | Web UI for browsing ZooKeeper nodes |
| Cassandra | 9042 | Time-series location storage for location-service |
| PostgreSQL | 5432 | Relational geocoding data store |
| Prometheus | 9090 | Metrics collection and storage |
| Grafana | 3000 | Metrics dashboards |

## Prerequisites

- [Docker](https://www.docker.com/) and [Docker Compose](https://docs.docker.com/compose/) v2+
- [Java 21](https://adoptium.net/) (for local builds)

## Build

Compile all subprojects and run tests:

```bash
./gradlew build
```

## Run

Start all services and infrastructure:

```bash
docker compose up -d
```

Nginx becomes available at `http://localhost:80` once all service health checks pass (allow ~2 minutes on first boot while Cassandra initialises).

## Load Testing

Run the built-in load client against the running stack (default: 60-second test):

```bash
docker compose --profile test up load-client
```

Override the duration:

```bash
LOAD_TEST_DURATION_SECONDS=120 docker compose --profile test up load-client
```

## Monitoring

| Tool | URL | Credentials |
|---|---|---|
| Grafana | http://localhost:3000 | admin / admin |
| Prometheus | http://localhost:9090 | — |
| ZooNavigator | http://localhost:9000 | — (auto-connects to ZooKeeper) |

## API Examples

All requests go through Nginx on port 80.

### Location Service

**Update a user's location:**

```bash
curl -X PUT http://localhost/api/v1/locations/user-42 \
  -H 'Content-Type: application/json' \
  -d '{
    "latitude": 37.7749,
    "longitude": -122.4194,
    "timestamp": "2026-04-10T12:00:00Z"
  }'
```

**Get a user's latest location:**

```bash
curl http://localhost/api/v1/locations/user-42
```

### Navigation Service

**Navigate between two addresses:**

```bash
curl "http://localhost/api/v1/navigate?originAddress=1600+Amphitheatre+Pkwy,+Mountain+View,+CA&destinationAddress=1+Infinite+Loop,+Cupertino,+CA&k=3"
```

**Navigate using coordinates:**

```bash
curl "http://localhost/api/v1/navigate?originLat=37.4220&originLng=-122.0841&destLat=37.3318&destLng=-122.0312&k=3"
```

### Geocoding Service

**Geocode an address to coordinates:**

```bash
curl "http://localhost/api/v1/geocode?address=1600+Amphitheatre+Pkwy,+Mountain+View,+CA"
```

**Reverse geocode coordinates to an address:**

```bash
curl "http://localhost/api/v1/reverse-geocode?lat=37.4220&lng=-122.0841"
```

### Route Planner Service

**Plan top-k routes between two points:**

```bash
curl "http://localhost/api/v1/routes?originLat=37.4220&originLng=-122.0841&destLat=37.3318&destLng=-122.0312&k=3"
```

### Shortest Path Service

**Find k-shortest paths between two points:**

```bash
curl "http://localhost/api/v1/shortest-paths?originLat=37.4220&originLng=-122.0841&destLat=37.3318&destLng=-122.0312&k=3"
```

### ETA Service

**Calculate estimated travel time for a sequence of waypoints:**

```bash
curl -X POST http://localhost/api/v1/eta \
  -H 'Content-Type: application/json' \
  -d '{
    "waypoints": [
      {"latitude": 37.4220, "longitude": -122.0841},
      {"latitude": 37.3900, "longitude": -122.0500},
      {"latitude": 37.3318, "longitude": -122.0312}
    ],
    "departureTime": "2026-04-10T09:00:00Z"
  }'
```

### Ranker Service

**Rank a list of route candidates and return the top-k:**

```bash
curl -X POST http://localhost/api/v1/rank \
  -H 'Content-Type: application/json' \
  -d '{
    "routes": [
      {
        "distance": 15200.5,
        "eta": 1320.0,
        "waypoints": [
          {"latitude": 37.4220, "longitude": -122.0841},
          {"latitude": 37.3318, "longitude": -122.0312}
        ]
      },
      {
        "distance": 18500.0,
        "eta": 1080.0,
        "waypoints": [
          {"latitude": 37.4220, "longitude": -122.0841},
          {"latitude": 37.3600, "longitude": -122.0600},
          {"latitude": 37.3318, "longitude": -122.0312}
        ]
      }
    ],
    "k": 1
  }'
```

### Map Tile Service

**Fetch tile metadata for a viewport:**

```bash
curl "http://localhost/api/v1/tiles?lat=37.4220&lng=-122.0841&zoom=14&viewportWidth=1280&viewportHeight=720"
```

## Project Structure

```
google-maps/
├── build.gradle                  # Root build configuration
├── settings.gradle               # Gradle multi-project settings
├── gradle.properties             # Dependency versions (Java 21, Spring Boot 3.4.4)
├── Dockerfile                    # Shared Docker image for all services
├── docker-compose.yml            # Full stack: services, infra, monitoring
│
├── location-service/             # PUT/GET /api/v1/locations/{userId}
├── navigation-service/           # GET  /api/v1/navigate
├── geocoding-service/            # GET  /api/v1/geocode, /api/v1/reverse-geocode
├── route-planner-service/        # GET  /api/v1/routes
├── shortest-path-service/        # GET  /api/v1/shortest-paths
├── eta-service/                  # POST /api/v1/eta
├── ranker-service/               # POST /api/v1/rank
├── map-tile-service/             # GET  /api/v1/tiles
├── load-client/                  # Gatling load test client (profile: test)
│
├── nginx/
│   └── nginx.conf                # Upstream definitions and location routing
├── prometheus/
│   └── prometheus.yml            # Scrape configs for all services
├── grafana/
│   ├── provisioning/             # Auto-provisioned data sources
│   └── dashboards/               # Pre-built dashboard JSON
├── init/
│   ├── cassandra/init.cql        # Keyspace and table DDL
│   └── postgres/init.sql         # Schema and seed data for geocoding
└── docs/
    └── google-maps-components.drawio  # System architecture diagram
```
