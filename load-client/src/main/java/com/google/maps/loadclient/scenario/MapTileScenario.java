package com.google.maps.loadclient.scenario;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ThreadLocalRandom;

public class MapTileScenario extends BaseScenario {

    private static final int[] ZOOM_LEVELS = {10, 12, 14, 16};

    public MapTileScenario() {
        super("MapTileScenario", 15);
    }

    @Override
    protected void execute(HttpClient client, String baseUrl, int threadIndex) throws Exception {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        double lat = 40.70 + rng.nextDouble() * 0.09;
        double lng = -74.02 + rng.nextDouble() * 0.09;
        int zoom = ZOOM_LEVELS[rng.nextInt(ZOOM_LEVELS.length)];

        String url = String.format(
                "%s/api/v1/tiles?lat=%s&lng=%s&zoom=%d&viewportWidth=800&viewportHeight=600",
                baseUrl,
                lat,
                lng,
                zoom
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
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
        return 300L;
    }
}
