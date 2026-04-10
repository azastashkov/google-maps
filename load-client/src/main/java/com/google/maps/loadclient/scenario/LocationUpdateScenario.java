package com.google.maps.loadclient.scenario;

import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class LocationUpdateScenario extends BaseScenario {

    private static final Gson GSON = new Gson();

    public LocationUpdateScenario() {
        super("LocationUpdateScenario", 50);
    }

    @Override
    protected void execute(HttpClient client, String baseUrl, int threadIndex) throws Exception {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        double lat = 40.70 + rng.nextDouble() * 0.10;
        double lng = -74.02 + rng.nextDouble() * 0.10;
        String userId = "user-" + (threadIndex + 1);
        String timestamp = Instant.now().toString();

        Map<String, Object> body = Map.of(
                "latitude", lat,
                "longitude", lng,
                "timestamp", timestamp
        );
        String json = GSON.toJson(body);

        String url = baseUrl + "/api/v1/locations/" + userId;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        long start = System.currentTimeMillis();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        long elapsed = System.currentTimeMillis() - start;

        totalRequests.incrementAndGet();
        latencies.add(elapsed);

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            successCount.incrementAndGet();
        } else {
            failureCount.incrementAndGet();
        }
    }

    @Override
    protected long delayMs() {
        return 2000L;
    }
}
