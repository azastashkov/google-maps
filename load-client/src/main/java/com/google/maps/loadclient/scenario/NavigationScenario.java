package com.google.maps.loadclient.scenario;

import com.google.maps.loadclient.SeedData;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class NavigationScenario extends BaseScenario {

    public NavigationScenario() {
        super("NavigationScenario", 10);
    }

    @Override
    protected void execute(HttpClient client, String baseUrl, int threadIndex) throws Exception {
        SeedData.Address origin = SeedData.random();
        SeedData.Address destination = SeedData.random();

        String encodedOrigin = URLEncoder.encode(origin.address(), StandardCharsets.UTF_8);
        String encodedDestination = URLEncoder.encode(destination.address(), StandardCharsets.UTF_8);

        String url = baseUrl + "/api/v1/navigate?originAddress=" + encodedOrigin
                + "&destinationAddress=" + encodedDestination;

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
        return 500L;
    }
}
